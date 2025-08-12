# Use an official Node runtime as a parent image.
# Using alpine for a smaller image size.
FROM node:20-alpine

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json (or yarn.lock)
# This is done as a separate step to leverage Docker cache.
# Dependencies will only be re-installed if these files change.
COPY package*.json ./

# Install app dependencies
RUN npm install

# Bundle app source
COPY . .

# Make port 3000 available to the world outside this container
EXPOSE 3000

# Run the app's development server when the container launches
CMD ["npm", "start"]