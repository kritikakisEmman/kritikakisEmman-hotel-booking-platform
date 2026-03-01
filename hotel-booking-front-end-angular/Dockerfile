# ─────────────────────────────────────────────────────
# STAGE 1: BUILD
# Node 16 image για να κάνουμε compile το Angular project
# ─────────────────────────────────────────────────────
FROM node:16-alpine AS build

WORKDIR /app

# Αντιγράφουμε πρώτα τα package files για cache (ίδια λογική με το pom.xml)
# Αν δεν άλλαξαν τα dependencies, δεν τρέχει ξανά npm install
COPY package.json package-lock.json ./
RUN npm ci

# Αντιγράφουμε τον υπόλοιπο κώδικα
COPY . .

# Κάνουμε production build — παράγει το /app/dist/bookingApp/
RUN npm run build

# ─────────────────────────────────────────────────────
# STAGE 2: SERVE
# nginx image — σερβίρει τα στατικά αρχεία και κάνει proxy τα API calls
# ─────────────────────────────────────────────────────
FROM nginx:alpine

# Αντιγράφουμε τα compiled αρχεία από το Stage 1
# στον φάκελο που σερβίρει ο nginx
COPY --from=build /app/dist/bookingApp /usr/share/nginx/html

# Αντιγράφουμε το nginx config ως template
COPY nginx.conf /etc/nginx/conf.d/default.conf.template

# Default τιμή για local Docker Compose
ENV BACKEND_URL=http://backend:8080

# Ο nginx ακούει στην πόρτα 80
EXPOSE 80

# Κατά την εκκίνηση: διαβάζουμε τον DNS resolver από το σύστημα (δουλεύει σε Docker ΚΑΙ Railway)
CMD ["/bin/sh", "-c", "NGINX_RESOLVER=$(awk '/^nameserver/{print $2; exit}' /etc/resolv.conf) && envsubst '${BACKEND_URL} ${NGINX_RESOLVER}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
