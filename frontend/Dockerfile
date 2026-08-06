# 阶段1：编译
FROM node:20 AS build
WORKDIR /app
COPY package*.json .
RUN npm install
COPY . .
RUN npm run build

# 阶段2：运行（nginx 托管静态文件）
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
