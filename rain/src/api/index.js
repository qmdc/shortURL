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

export const login=(data)=>{
    return request({
        method:'POST',
        url:`/user/login`,
        data:data,
    })
}

export const register=(data)=>{
    return request({
        method:'POST',
        url:`/user/register`,
        data:data,
    })
}

export const getUserInfo=()=>{
    return request({
        method:'GET',
        url:`/user/info`,
    })
}

export const getUserShortUrls=()=>{
    return request({
        method:'GET',
        url:`/user/shorturls`,
    })
}

export const deleteShortUrl=(id)=>{
    return request({
        method:'DELETE',
        url:`/shorturl/${id}`,
    })
}



