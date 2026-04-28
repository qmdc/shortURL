import Vuex from 'vuex'
import Vue from "vue";

Vue.use(Vuex)

const about = {
    namespaced: true,
    actions:{
        addOne(context,value) {
            context.commit("ADDONE",value)
        },
    },
    mutations:{
        ADDONE(state,value){
            state.num += value
        },
    },
    state:{
        num:1
    },
    getters:{
        tenNum(state) {
            return state.num * 10
        }
    }
}

const user = {
    namespaced: true,
    state:{
        token: localStorage.getItem('token') || '',
        userInfo: JSON.parse(localStorage.getItem('user')) || null
    },
    mutations:{
        SET_TOKEN(state, token){
            state.token = token
            if (token) {
                localStorage.setItem('token', token)
            } else {
                localStorage.removeItem('token')
            }
        },
        SET_USER_INFO(state, userInfo){
            state.userInfo = userInfo
            if (userInfo) {
                localStorage.setItem('user', JSON.stringify(userInfo))
            } else {
                localStorage.removeItem('user')
            }
        },
        LOGOUT(state){
            state.token = ''
            state.userInfo = null
            localStorage.removeItem('token')
            localStorage.removeItem('user')
        }
    },
    actions:{
        login({commit}, data){
            commit('SET_TOKEN', data.token)
            commit('SET_USER_INFO', {
                userId: data.userId,
                username: data.username,
                email: data.email
            })
        },
        logout({commit}){
            commit('LOGOUT')
        }
    },
    getters:{
        isLoggedIn: state => !!state.token,
        username: state => state.userInfo ? state.userInfo.username : '',
        userId: state => state.userInfo ? state.userInfo.userId : ''
    }
}


export default new Vuex.Store({
    modules:{
        about: about,
        user: user
    }
})