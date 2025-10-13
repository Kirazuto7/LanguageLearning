module.exports = {
  devServer: {
    client: {
      logging: 'none', // This will suppress all HMR-related console messages, including the WebSocket error.
    },
  },
};
