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


export default new Vuex.Store({
    modules:{
        about: about
    }
})