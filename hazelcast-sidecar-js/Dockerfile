FROM node:18.14.2-alpine3.17
WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

ENTRYPOINT ["npm", "start"]
EXPOSE 8080
