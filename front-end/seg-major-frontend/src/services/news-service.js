import BackendService from './backend-service'

export default {
  newsPromise: null,

  async getAllArticles() {
    if (!this.newsPromise) {
      this.newsPromise = BackendService.getAllArticles()
                                       .then(function(res) {
        let data = res.data;
        data.forEach(news => news.date = new Date(news.date));
        return data;
      });
    }
    
    return await this.newsPromise;
  },

  async getArticle(slug) {
    return (await this.getAllArticles())
            .find(elem => elem.slug === slug) ?? null;
  },

  async createArticle(article) {
    const res = await BackendService.createArticle(article);
    this.newsPromise = null;
    return res.data;
  },

  async updateArticle(article) {
    const res = await BackendService.updateArticle(article);
    this.newsPromise = null;
    return res.data;
  },

  async deleteArticle(article) {
    const res = await BackendService.deleteArticle(article);
    this.newsPromise = null;
    return res.data;
  },
}
