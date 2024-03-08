// 引入pinia
import { defineStore } from 'pinia'
// 定义 store 并导出
export const useAdminStore = defineStore('admin', {
    // 定义状态【数据】
    state: () => ({
        adminId: undefined,
        nickname: undefined,
        avatar: undefined,
    }),
    // 获取数据的方法
    getters: {
        Number: (state) => state.adminId,
        String: (state) => state.avatar,
        String: (state) => state.nickname
    },

    // 修改数据方法
    actions: {
        setAdminInfo(data) {
            this.adminId = data.adminId;
            this.nickname = data.nickname;
            this.avatar = data.avatar;
        },
        setAdminAvatar(avatar) {
            this.avatar = avatar;
            this.$patch({ avatar });
        },
    },

    // 使用持久化
    persist: {
    	enabled: true,
        storage: localStorage,
        key: 'adminInfo',
        path: ['adminId','nickname','avatar']
    }
})