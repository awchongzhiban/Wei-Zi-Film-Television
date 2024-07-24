"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.addExtractThemeLinkTag = void 0;
var _someLoaderUtils = require("@zougt/some-loader-utils");
const addExtractThemeLinkTag = ({
  html,
  defaultOptions,
  allmultipleScopeVars,
  buildCommand,
  config
}) => {
  // 向html中添加抽取的主题css文件的link标签，并在html标签中添加 calssName
  let newHtml = html;
  const tags = [];
  const {
    themeLinkTagInjectTo,
    extract,
    removeCssScopeName,
    themeLinkTagId,
    outputDir,
    defaultScopeName,
    customThemeCssFileName
  } = defaultOptions;
  if (Array.isArray(allmultipleScopeVars) && allmultipleScopeVars.length) {
    const scopeName = defaultScopeName || allmultipleScopeVars[0].scopeName;
    if (buildCommand !== "build" || !removeCssScopeName) {
      newHtml = (0, _someLoaderUtils.addScopnameToHtmlClassname)(newHtml, scopeName);
    }
    if (buildCommand === "build" && extract && themeLinkTagId) {
      const filename = (typeof customThemeCssFileName === "function" ? customThemeCssFileName(scopeName) : "") || scopeName;
      const linkHref = `${config.base || ""}/${outputDir || config.build.assetsDir}/${filename}.css`.replace(/(^|[^:])\/+/g, "$1/");
      const tag = {
        tag: "link",
        attrs: {
          rel: "stylesheet",
          href: linkHref,
          id: themeLinkTagId
        },
        injectTo: themeLinkTagInjectTo
      };
      tags.push(tag);
    }
  }
  return {
    html: newHtml,
    tags
  };
};
exports.addExtractThemeLinkTag = addExtractThemeLinkTag;