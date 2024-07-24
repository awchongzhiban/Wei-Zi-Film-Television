import * as f from "vue";
import { defineComponent as h, createVNode as j, Fragment as Q, ref as G, toRefs as H, computed as E, withDirectives as B, mergeProps as b, unref as u, resolveDirective as W, isVNode as X } from "vue";
import { ElDescriptions as R, ElDescriptionsItem as F } from "element-plus";
const J = { data: { type: Array, default: [] }, columns: { type: Array, default: [] }, loading: { type: Object, default: () => ({ load: !1, text: "Loading...", svg: "", spinner: "", svgViewBox: "", background: "" }) }, align: { type: String, default: "left" }, labelAlign: { type: String, default: "" }, ...R.props }, S = h({ name: "Renderer", props: { render: { type: Function }, params: { type: Object } }, setup: (M) => () => j(Q, null, [M.render(M.params)]) });
var P = Object.defineProperty, q = Object.getOwnPropertyDescriptor, K = Object.getOwnPropertyNames, _ = Object.prototype.hasOwnProperty, C = (M, t, l, c) => {
  if (t && typeof t == "object" || typeof t == "function")
    for (let o of K(t))
      !_.call(M, o) && o !== l && P(M, o, { get: () => t[o], enumerable: !(c = q(t, o)) || c.enumerable });
  return M;
}, w, U, $ = typeof document < "u", a = {};
function Y(M, { target: t = $ ? document.body : void 0 } = {}) {
  let l = document.createElement("textarea"), c = document.activeElement;
  l.value = M, l.setAttribute("readonly", ""), l.style.contain = "strict", l.style.position = "absolute", l.style.left = "-9999px", l.style.fontSize = "12pt";
  let o, s = document.getSelection();
  s && s.rangeCount > 0 && (o = s.getRangeAt(0)), t == null || t.append(l), l.select(), l.selectionStart = 0, l.selectionEnd = M.length;
  let z = !1;
  try {
    z = document.execCommand("copy");
  } catch (L) {
    throw new Error(L.message);
  }
  return l.remove(), o && s && (s.removeAllRanges(), s.addRange(o)), c instanceof HTMLElement && c.focus(), z;
}
((M, t) => {
  for (var l in t)
    P(M, l, { get: t[l], enumerable: !0 });
})(a, { Vue: () => f }), C(a, w = f, "default"), U && C(U, w, "default");
const I = h({ name: "PureDescriptions", props: J, setup(M, { slots: t, attrs: l }) {
  const c = G(-1), o = new URL("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgY2xhc3M9Imljb24iIHZpZXdCb3g9IjAgMCAxMDI0IDEwMjQiPjxwYXRoIGZpbGw9IiM0MDllZmYiIGQ9Ik01ODQuMjM1IDk5NC4zNDVIMjMxLjYwM2MtNzQuNTY1IDAtMTM1LjE1LTYwLjU4NC0xMzUuMTUtMTM1LjE1VjM2MC41NGMwLTc0LjU2NSA2MC41ODUtMTM1LjE1IDEzNS4xNS0xMzUuMTVoMzUyLjYzMmM3NC41NjYgMCAxMzUuMTUgNjAuNTg1IDEzNS4xNSAxMzUuMTV2NDk4LjY1NmMwIDc0LjU2Ni02MC41ODQgMTM1LjE1LTEzNS4xNSAxMzUuMTVNMjMxLjYwMyAzMDMuMDYyYy0zMS44NDYgMC01Ny40NzcgMjUuNjMxLTU3LjQ3NyA1Ny40Nzd2NDk4LjY1NmMwIDMxLjg0NiAyNS42MzEgNTcuNDc4IDU3LjQ3NyA1Ny40NzhoMzUyLjYzMmMzMS44NDYgMCA1Ny40NzgtMjUuNjMyIDU3LjQ3OC01Ny40NzhWMzYwLjU0YzAtMzEuODQ2LTI1LjYzMi01Ny40NzctNTcuNDc4LTU3LjQ3N3oiLz48cGF0aCBmaWxsPSIjNDA5ZWZmIiBkPSJNODMyLjAxIDc5MS42MmMtMjEuNzQ4IDAtMzguODM2LTE3LjA4OC0zOC44MzYtMzguODM2di00NTcuNDljMC04MC43NzktNjUuMjQ1LTE0Ni4wMjQtMTQ2LjAyNC0xNDYuMDI0SDMzNi40NmMtMjEuNzQ4IDAtMzguODM2LTE3LjA4OC0zOC44MzYtMzguODM2czE3LjA4OC0zOC44MzYgMzguODM3LTM4LjgzNkg2NDcuMTVjMTIzLjQ5OSAwIDIyMy42OTYgMTAwLjE5NyAyMjMuNjk2IDIyMy42OTZ2NDU3LjQ5YzAgMjAuOTcyLTE3LjA4OCAzOC44MzYtMzguODM2IDM4LjgzNiIvPjwvc3ZnPg==", self.location).href, s = new URL("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgY2xhc3M9Imljb24iIHZpZXdCb3g9IjAgMCAxMDI0IDEwMjQiPjxwYXRoIGZpbGw9IiM2N2MyM2EiIGQ9Ik0zNTEuODA5IDg0Ni43NTJjLTE5LjE2IDAtMzcuMjU0LTcuNDUtNTEuMDkxLTIxLjI4OC0xLjA2NS0xLjA2NC0xLjA2NS0yLjEyOS0yLjEyOS0yLjEyOUwzOS45NDEgNTA1LjA4MWMtMTEuNzA4LTEzLjgzNy05LjU4LTM1LjEyNSA1LjMyMi00Ni44MzMgMTMuODM3LTExLjcwOCAzNS4xMjUtOS41OCA0Ni44MzQgNS4zMjJsMjU2LjUxOSAzMTYuMTI1YzIuMTI4IDIuMTI5IDQuMjU3IDIuMTI5IDcuNDUtMS4wNjRsNTY2LjI1OC02MDIuNDQ3YzEyLjc3My0xMy44MzcgMzQuMDYtMTMuODM3IDQ2LjgzMy0xLjA2NSAxMy44MzcgMTIuNzczIDEzLjgzNyAzNC4wNiAxLjA2NSA0Ni44MzRMNDAyLjkgODI1LjQ2NGMtMTMuODM4IDEzLjgzNy0zMS45MzIgMjEuMjg4LTUxLjA5MSAyMS4yODgiLz48L3N2Zz4=", self.location).href, { data: z, columns: L, align: Z, labelAlign: k, loading: N } = H(M), m = { title: () => (t == null ? void 0 : t.title) && t.title({ props: M, attrs: l }) }, A = { extra: () => (t == null ? void 0 : t.extra) && t.extra({ props: M, attrs: l }) }, V = (t == null ? void 0 : t.title) && !(t != null && t.extra) ? m : (t == null ? void 0 : t.extra) && !(t != null && t.title) ? A : (t == null ? void 0 : t.title) && (t == null ? void 0 : t.extra) ? Object.assign(m, A) : null, { copied: p, update: x } = ((i = "") => {
    let e = (0, a.shallowRef)(i), r = (0, a.shallowRef)(!1);
    return (0, a.watch)(e, (n = i) => {
      (n = (n = (0, a.isProxy)(n) || (0, a.isRef)(n) ? (0, a.unref)(n) : n).trim().length === 0 ? i : n).length > 0 ? r.value = Y(n) : r.value = !1;
    }, { flush: "sync" }), { clipboardValue: e, copied: r, update: (n) => {
      e.value = (0, a.isProxy)(n) || (0, a.isRef)(n) ? (0, a.unref)(n) : n;
      let y = e.value.trim().length === 0 ? i : e.value;
      y.length > 0 ? r.value = Y(y) : r.value = !1;
    } };
  })();
  function T(i, e) {
    p.value || (c.value = e, function(r) {
      return r && Array.isArray(r);
    }(i) ? x(i[0]) : x(i), ((r = 20) => new Promise((n) => setTimeout(n, r)))(600).then(() => p.value = !p.value));
  }
  const v = E(() => ({ cursor: "pointer", marginLeft: "4px", verticalAlign: "sub" })), O = E(() => (i) => c.value === i && p.value ? s : o);
  return () => {
    var i;
    return B(j(R, b(M, l, { "element-loading-text": (i = u(N).text) != null ? i : "Loading...", "element-loading-svg": u(N).svg, "element-loading-spinner": u(N).spinner, "element-loading-svg-view-box": u(N).svgViewBox, "element-loading-background": u(N).background }), { default: () => [u(L).map((e, r) => {
      let n = u(z).map((g) => g[e == null ? void 0 : e.prop]);
      const y = { default: () => {
        var g;
        return e != null && e.cellRenderer ? j(S, { render: e.cellRenderer, params: { props: M, attrs: l, index: r, value: n[0] } }, null) : e != null && e.slot ? (g = t == null ? void 0 : t[e.slot]) == null ? void 0 : g.call(t, { props: M, attrs: l, index: r, value: n[0] }) : j(Q, null, e != null && e.value ? [u(e.value), u(e == null ? void 0 : e.copy) && j("img", { src: O.value(r), style: v.value, onClick: () => T(u(e.value), r) }, null)] : [n, (e == null ? void 0 : e.copy) && j("img", { src: O.value(r), style: v.value, onClick: () => T(n, r) }, null)]);
      } }, d = e != null && e.labelRenderer ? { label: () => j(S, { render: e.labelRenderer, params: { props: M, attrs: l, index: r, value: n[0] } }, null), ...y } : y;
      return function(g) {
        return typeof g == "function";
      }(e == null ? void 0 : e.hide) && (e == null ? void 0 : e.hide(l)) ? e == null ? void 0 : e.hide(l) : j(F, b(e, { key: r, align: e.align ? e.align : u(Z), labelAlign: e.labelAlign ? e.labelAlign : u(k) }), typeof (D = d) == "function" || Object.prototype.toString.call(D) === "[object Object]" && !X(D) ? d : { default: () => [d] });
      var D;
    })], ...V }), [[W("loading"), u(N).load]]);
  };
} }), le = Object.assign(I, { install: function(M) {
  M.component(I.name, I);
} });
export {
  le as PureDescriptions,
  le as default
};
