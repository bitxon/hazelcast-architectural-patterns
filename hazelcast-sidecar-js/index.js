import express from "express";
import { setTimeout } from "timers/promises";
import { Client } from "hazelcast-client";


const port = 8080;
const app = express();
const hzConfig = {
  clusterName: "hzcluster",
  network: {
    clusterMembers: ["localhost:5701"],
  },
  connectionStrategy: {
    connectionRetry: {
      clusterConnectTimeoutMillis: 10000,
    },
  },
};


async function start() {
  const hzClient = await Client.newHazelcastClient(hzConfig);
  const hzCpSubsystem = await hzClient.getCPSubsystem();
  const mainCache = await hzClient.getMap("main-cache");
  const lockedCache = await hzClient.getMap("locked-cache");

  app.get("/cache/:key", async (req, res) => {
    const { key } = req.params;
    const value = await mainCache.get(key);
    res.send(value);
  });

  app.put("/cache/:key/:value", async (req, res) => {
    const { key, value } = req.params;
    await mainCache.put(key, value);
    res.send(value);
  });

  app.get("/fenced-lock-cache/:key", async (req, res) => {
    const { key } = req.params;

    const lock = await hzCpSubsystem.getLock(key);
    const fence = await lock.tryLock(6000);
    if (fence !== undefined) {
      try {
        const value = await lockedCache.get(key);
        res.send(value);
      } finally {
        await lock.unlock(fence);
      }
    } else {
      res.status(408).send();
    }
  });

  app.put("/fenced-lock-cache/:key/:value", async (req, res) => {
    const { key, value } = req.params;

    const lock = await hzCpSubsystem.getLock(key);
    const fence = await lock.tryLock(6000);
    if (fence !== undefined) {
      try {
        await lockedCache.remove(key);
        await setTimeout(5000); // pretend that this is long operation
        await lockedCache.put(key, value);
        res.send(value);
      } finally {
        await lock.unlock(fence);
      }
    } else {
      res.status(408).send();
    }
  });

  app.listen(port, () => {
    console.log(`your server is running on port ${port}`);
  });
}


start()
  .then(() => {
    console.log('Application started');
  })
  .catch((error) => {
    console.log('Application failed to start', error);
  });
