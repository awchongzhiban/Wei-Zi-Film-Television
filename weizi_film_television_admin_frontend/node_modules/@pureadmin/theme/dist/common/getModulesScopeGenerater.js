"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getModulesScopeGenerater = void 0;
var _stringHash = _interopRequireDefault(require("string-hash"));
function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
const getModulesScopeGenerater = opt => {
  return (name, filename, css) => {
    if ((opt.multipleScopeVars || []).some(item => item.scopeName === name)) {
      return name;
    }
    if (typeof opt.generateScopedName === "function") {
      return opt.generateScopedName(name, filename, css);
    }
    const i = css.indexOf(`.${name}`);
    const lineNumber = css.substr(0, i).split(/[\r\n]/).length;
    const hash = (0, _stringHash.default)(css).toString(36).substr(0, 5);
    return `_${name}_${hash}_${lineNumber}`;
  };
};
exports.getModulesScopeGenerater = getModulesScopeGenerater;