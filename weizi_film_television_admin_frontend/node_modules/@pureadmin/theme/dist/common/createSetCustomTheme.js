"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.createSetCustomTheme = void 0;
var _someLoaderUtils = require("@zougt/some-loader-utils");
// import pack from "../../package.json";

const createSetCustomTheme = options => {
  const {
    styleTagId,
    defaultPrimaryColor,
    customThemeOutputPath,
    includeStyleWithColors,
    buildCommand,
    cacheThemeStyleContent
  } = options;
  return (0, _someLoaderUtils.getThemeStyleContent)().then(({
    styleContent,
    themeRuleValues
  }) => {
    if (!cacheThemeStyleContent || cacheThemeStyleContent !== styleContent) {
      return (0, _someLoaderUtils.createSetCustomThemeFile)({
        defaultPrimaryColor,
        customThemeOutputPath,
        styleTagId,
        includeStyleWithColors,
        styleContent,
        themeRuleValues,
        importUtils: buildCommand !== "build",
        appendedContent: buildCommand === "build" ? "" : "\nexport default setCustomTheme;",
        preAppendedContent: "/**This file created by @pureadmin/theme,you can not modify it.*/\n"
      });
    }
    return Promise.resolve();
  });
};
exports.createSetCustomTheme = createSetCustomTheme;