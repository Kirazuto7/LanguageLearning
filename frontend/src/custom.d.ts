declare  module '*.module.css';

declare module '*.module.scss' {
  const classes: { [key: string]: string };
  export default classes;
}

