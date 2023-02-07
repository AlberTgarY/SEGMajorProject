import BackendService from './backend-service'

export default {
  appInfoPromise: null,

  async getAppInfo() {
    if (!this.appInfoPromise) {
      this.appInfoPromise = BackendService.getAppInfo()
                                          .then(function(res) {
        return res.data;
      });
    }
    
    return await this.appInfoPromise;
  },

  async updateAppInfo(info) {
    const res = await BackendService.updateAppInfo(info);
    this.appInfoPromise = null;
    return res.data;
  },
}
