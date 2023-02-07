<template>
  <div id="admin-news">
    <div class="expanded-scrollable-area">
      <section class="section">
        <div class="custom-content-container">
          <h1 class="title">All News Articles</h1>
          <transition name="fade" mode="out-in">
            <loading-spinner v-if="loading" style="margin-top: 50px;" />
            <div v-else>
              <router-link
                v-for="article of news"
                :key="article.primaryKey"
                class="is-block"
                style="margin-bottom: 20px"
                :to="article.slug"
                append
              >
                <news-card :newsItem="article" class="news-card" />
              </router-link>
            </div>
          </transition>
        </div>
      </section>
    </div>

    <router-link to="new" append class="button is-primary floating-add-button">
      <i class="material-icons" style="font-size: 48px;">add</i>
    </router-link>
  </div>
</template>

<script>
import LoadingSpinner from "@/components/LoadingSpinner";
import NewsService from "@/services/news-service";
import NewsCard from "@/components/NewsCard.vue";

export default {
  components: {
    LoadingSpinner,
    NewsCard
  },
  data() {
    return {
      news: null,
      loading: true
    };
  },
  metaInfo: {
    title: 'News'
  },
  async mounted() {
    this.loading = true;
    this.news = await NewsService.getAllArticles();
    this.loading = false;
  }
};
</script>

<style lang="scss" scoped>
#admin-news {
  height: 100%;
}

.expanded-scrollable-area {
  overflow-y: scroll;
}
</style>