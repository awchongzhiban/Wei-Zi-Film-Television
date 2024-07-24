import { PluginOption } from 'vite';
export { PluginOption } from 'vite';

interface Options {
    /** export the modified `vue-router` file to the project root directory. default `false`. when `true`, `router.txt` is exported. Of course, you can customize the export file name by filling in the `string` */
    txt?: boolean | string;
}

/** A vite plugin in development environment that remove dynamic router refresh warning: `No match found for location with path` */
declare function removeNoMatch(options?: Partial<Options>): PluginOption;

export { removeNoMatch as default };
