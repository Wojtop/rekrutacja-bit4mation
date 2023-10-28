FROM node
COPY /public /public
COPY /src /src
COPY package.json package.json
RUN npm install
ENTRYPOINT ["npm", "start"]