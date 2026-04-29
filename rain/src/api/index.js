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

export const getAnalyticsOverview=()=>{
    return request({
        method:'GET',
        url:`/analytics/overview`,
    })
}

export const getAnalyticsRecords=(page, size, days)=>{
    let params = []
    if(page) params.push(`page=${page}`)
    if(size) params.push(`size=${size}`)
    if(days) params.push(`days=${days}`)
    const query = params.length > 0 ? '?' + params.join('&') : ''
    return request({
        method:'GET',
        url:`/analytics/records${query}`,
    })
}

export const getVisitStats=(mapId, days)=>{
    const query = days ? `?days=${days}` : ''
    return request({
        method:'GET',
        url:`/analytics/stats/${mapId}${query}`,
    })
}

export const getVisitRecords=(mapId, page, size)=>{
    let params = []
    if(page) params.push(`page=${page}`)
    if(size) params.push(`size=${size}`)
    const query = params.length > 0 ? '?' + params.join('&') : ''
    return request({
        method:'GET',
        url:`/analytics/records/${mapId}${query}`,
    })
}

export const getHourlyTrend=(mapId, days)=>{
    const query = days ? `?days=${days}` : ''
    return request({
        method:'GET',
        url:`/analytics/trend/hourly/${mapId}${query}`,
    })
}

export const getDailyTrend=(mapId, days)=>{
    const query = days ? `?days=${days}` : ''
    return request({
        method:'GET',
        url:`/analytics/trend/daily/${mapId}${query}`,
    })
}

export const getWeeklyTrend=(mapId, weeks)=>{
    const query = weeks ? `?weeks=${weeks}` : ''
    return request({
        method:'GET',
        url:`/analytics/trend/weekly/${mapId}${query}`,
    })
}

export const getRegionDistribution=(mapId, days)=>{
    const query = days ? `?days=${days}` : ''
    return request({
        method:'GET',
        url:`/analytics/region/${mapId}${query}`,
    })
}

export const getDeviceDistribution=(mapId, days)=>{
    const query = days ? `?days=${days}` : ''
    return request({
        method:'GET',
        url:`/analytics/device/${mapId}${query}`,
    })
}

export const getBrowserDistribution=(mapId, days)=>{
    const query = days ? `?days=${days}` : ''
    return request({
        method:'GET',
        url:`/analytics/browser/${mapId}${query}`,
    })
}



