import axios from 'axios'
import router from '@/router'

const api = axios.create({
  baseURL: 'https://getnova.app'
})

// request interceptor
api.interceptors.request.use(function (config) {
  const apiKey = localStorage.getItem('api-key');
  config.headers['X-API-Key'] = apiKey;
  return config;
}, function (error) {
  return Promise.reject(error);
});

// response interceptor
api.interceptors.response.use(function (response) {
  return response;
}, function (error) {
  if (!error.response) {
    // Network error
  } else if (error.response.status === 401) {
    // session has expired, api key no longer valid so LOG OUT
    const apiKey = localStorage.getItem('api-key')
    if (apiKey != null) {
      // if apiKey had a value, session has expired
      router.push('/login?exp=true')
    } else {
      if (window.location.pathname != '/login') {
        router.push('/login')
      }
    }

    localStorage.removeItem('api-key')
  }

  return Promise.reject(error);
});

export default {
  getAppInfo() {
    return api.get('/appinfo')
  },

  updateAppInfo(info) {
    return api.put('/appinfo', info)
  },

  login(email, password) {
    return api.post('/user/login', { email, password })
  },

  logout() {
    return api.get('/user/logout')
  },

  getResetRequest(email) {
    return api.post( '/user/password_reset_request' , { 'string': email } ) 
  },
  
  resetPassword(token, password) {
    return api.put( '/user/password_reset_change' , { token, password } ) 
  },
  
  getUserName() {
    return api.get('/user/name')
  },

  createUser(user) {
    return api.post('/user/create', user)
  },

  getUserDetails() {
    return api.get('/user/user_details')
  },

  getAllUsers() {
    return api.get('/user')
  },

  changeUserName(name) {
    return api.put('/user/change_name', { 'string': name })
  },

  changeUserEmail(email) {
    return api.put('/user/change_email', { 'string': email })
  },

  changeUserPassword(newPassword) {
    return api.put('/user/change_password', { 'string': newPassword })
  },

  deleteUser(user) {
    return api.delete('/user/delete', { data: user })
  },

  createSite(site) {
    return api.post('/sites', site)
  },

  getAllSites() {
    return api.get('/sites')
  },

  getSite(siteSlug) {
    return api.get(`/sites/${siteSlug}`)
  },

  updateSite(site) {
    return api.put(`/sites`, site)
  },

  deleteSite(site) {
    return api.delete(`/sites/${site.slug}`)
  },

  getAllPages(siteSlug) {
    return api.get(`/sites/${siteSlug}/pages`)
  },

  getPage(siteSlug, pageSlug) {
    return api.get(`/sites/${siteSlug}/pages/${pageSlug}`)
  },

  createPage(page) {
    return api.post(`/sites/${page.site.slug}/pages`, {
      "site": page.site.slug,
      "slug": page.slug,
      "index": page.index,
      "title": page.title,
      "content": page.content
    })
  },

  updatePage(page) {
    return api.put(`/sites/${page.site.slug}/pages`, {
      "primaryKey": page.primaryKey,
      "site": page.site.slug,
      "slug": page.slug,
      "index": page.index,
      "title": page.title,
      "content": page.content
    })
  },

  updatePageIndexes(pages) {
    if (pages && pages.length > 0) {
      const siteSlug = pages[0].site.slug;
      return api.patch(`/sites/${siteSlug}/page-indices`, pages)
    }
  },

  deletePage(page) {
    return api.delete(`/sites/${page.site.slug}/pages/${page.slug}`)
  },

  getAllArticles() {
    return api.get('/news')
  },

  createArticle(data) {
    return api.post('/news', data)
  },

  updateArticle(data) {
    return api.put('/news', data)
  },

  deleteArticle(data) {
    return api.delete(`/news/${data.slug}`)
  },

  getAllMedicines() {
    return api.get('/medicines')
  },

  createMedicine(data) {
    return api.post('/medicines', data)
  },

  updateMedicine(data) {
    return api.put('/medicines', data)
  },

  deleteMedicine(data) {
    return api.delete(`/medicines/${data.primaryKey}`)
  },

  uploadImage(imageData) {
    return api.post('/images', imageData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
