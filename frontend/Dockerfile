FROM nginx:stable-alpine

WORKDIR /usr/share/nginx/html

COPY index.html .
COPY style.css .
COPY app.js .

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"] 