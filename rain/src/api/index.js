import request from './request'

export const create=(data)=>{
    return request({
        method:'GET',
        url:`create/shorturl?url=${data}`,
    })
}

export const recharge=(data)=>{
    return request({
        method:'GET',
        url:`recharge?url=${data}`,
    })
}

export const top10=()=>{
    return request({
        method:'GET',
        url:`top10`,
    })
}

export const record=()=>{
    return request({
        method:'GET',
        url:`record`,
    })
}



