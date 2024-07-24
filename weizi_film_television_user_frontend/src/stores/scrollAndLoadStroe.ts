import { defineStore } from 'pinia';

export const useScrollAndLoadStore = defineStore('scrollAndLoad', {
    state: () => ({
        isHasCustomMethod: false,
        isFinish: false,
        isLoading: false,
        params: {},
        resultData: [],
        customMethod: null as ((...args: any[]) => any) | null, // 存储函数引用
    }),
    actions: {
        setIsHasCustomMethod(value: boolean) {
            this.isHasCustomMethod = value;
        },
        setIsFinish(value: boolean) {
            this.isFinish = value;
        },
        setIsLoading(value: boolean) {
            this.isLoading = value;
        },
        setParams(params: any) {
            this.params = params;
        },
        setResultData(data: any) {
            for (let i = 0; i < data.length; i++){
                this.resultData.push(data[i]);
            }
        },
        setCustomMethod(method: (...args: any[]) => any) {
            this.customMethod = method;
        },
        resetScrollAndLoadStore() {
            this.isHasCustomMethod = false;
            this.isFinish = false;
            this.isLoading = false;
            this.params = {};
            this.resultData = [];
            this.customMethod = null;
        },
        resetIsLoading() {
            this.isLoading = false;
        },
        resetIsFinish() {
            this.isFinish = false;
        },
        resetResultData() {
            this.resultData = [];
        }
    },
});
