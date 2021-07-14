FROM node:12

WORKDIR /src/front_app

COPY package*.json ./

RUN npm install

COPY . /src/front_app

EXPOSE 8080

CMD ["npm", "run", "serve"]