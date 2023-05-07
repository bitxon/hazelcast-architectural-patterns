FROM node:20.1.0-alpine3.17
WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

ENTRYPOINT ["npm", "start"]
EXPOSE 8080
