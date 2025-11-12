# Use the official Flutter image
FROM ghcr.io/cirruslabs/flutter:stable

# Set working directory
WORKDIR /app

# Copy pubspec files
COPY pubspec.yaml pubspec.lock ./

# Get dependencies
RUN flutter pub get

# Copy the rest of the app
COPY . .

# Generate code for JSON serialization
RUN flutter packages pub run build_runner build --delete-conflicting-outputs

# Enable web support
RUN flutter config --enable-web

# Build the app for web
RUN flutter build web

# Use nginx to serve the Flutter web app
FROM nginx:alpine

# Copy built app to nginx
COPY --from=0 /app/build/web /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port
EXPOSE 3000

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
