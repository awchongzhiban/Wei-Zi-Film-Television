<h1 align="center">vite-plugin-router-warn</h1>
<p align="center">A vite plugin in development environment that remove dynamic router refresh warning: No match found for location with path</p>

<p align="center">
<a href="https://www.npmjs.com/package/vite-plugin-router-warn" target="__blank"><img src="https://img.shields.io/npm/v/vite-plugin-router-warn?color=67C23A&label=" alt="NPM version"></a>
</p>

## 🤔 Origin

Solve issues related to https://github.com/vuejs/router/issues/359   
When the author provides the relevant configuration to turn off the warning, `vite-plugin-router-warn` will no longer be needed.  
Only enabled in the development environment, only processes `vue-router` files and only runs once when the service is started or restarted, with extremely low performance consumption.  

<img src="https://xiaoxian521.github.io/hyperlink/img/vuerouter.jpg" alt="vuerouter" width="600" />

## 🦾 Configurable

| configuration | must | meaning                                                      | type             | example                        |
| :------------ | :--- | :----------------------------------------------------------- | :--------------- | :----------------------------- |
| `txt`         | No   | export the modified `vue-router` file to the project root directory. default `false`. when `true`, `router.txt` is exported. Of course, you can customize the export file name by filling in the `string` (turn on when comparing files) | `boolean`、`string` | `removeNoMatch({ txt: true })` |

## 📦 Install

```bash
# npm
npm install vite-plugin-router-warn -D

# or yarn
yarn add vite-plugin-router-warn -D

# or pnpm
pnpm add vite-plugin-router-warn -D
```

## 📕 Usage

```ts
// vite.config.ts

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import removeNoMatch from "vite-plugin-router-warn";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue(), removeNoMatch()]
});
```

## License

[MIT © 2023-present, xiaoxian521](./LICENSE)
