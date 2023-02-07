import BackendService from './backend-service'

export default {
  async login(username, password) {
    const res = await BackendService.login(username, password)
    localStorage.setItem('api-key', res.data);
    return res.data
  },

  async logout() {
    await BackendService.logout()
    localStorage.removeItem('api-key')
  },

  async createUser(user) {
    const res = await BackendService.createUser(user)
    return res.data
  },

  async getUserName() {
    const res = await BackendService.getUserDetails()
    return res.data.name
  },

  async getUserDetails() {
    const res = await BackendService.getUserDetails()
    return res.data
  },

  async getResetRequest(email){
    await BackendService.getResetRequest(email)
  },

  async resetPassword(token, password) {
    const res = await BackendService.resetPassword(token, password)
    return res.data
  },

  isAuthenticated() {
    const apiKey = localStorage.getItem('api-key')
    return apiKey !== null
  },

  async getAllUsers() {
    const res = await BackendService.getAllUsers()
    return res.data
  },

  async changeName(name) {
    await BackendService.changeUserName(name)
  },

  async changeEmail(email) {
    await BackendService.changeUserEmail(email)
  },

  async changePassword(user) {
    const res = await BackendService.changeUserPassword(user)
    return res.data
  },

  async deleteAccount(user) {
    const res = await BackendService.deleteUser(user)
    return res.data;
  }
}
