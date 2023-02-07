<template>
  <div class="card bulletin-board">
    <div class="header">
      <p class="title is-4">Bulletin Board</p>
      <p class="subtitle">Read our latest news and guidance</p>
    </div>

    <loading-spinner class="loading-spinner" v-if="loading" />
    <div class="no-news-message has-text-centered" v-else-if="items.length === 0">No news items</div>

    <router-link v-for="item in displayItems" :key="item.slug" :to="`/news/${item.slug}`">
      <news-card :newsItem="item" />
    </router-link>

    <router-link to="/news">
      <button class="button is-fullwidth is-light is-medium">View all</button>
    </router-link>
  </div>
</template>

<script>
import LoadingSpinner from '@/components/LoadingSpinner.vue';
import NewsCard from '@/components/NewsCard.vue';
import NewsService from '@/services/news-service';
import ArraySlice from '@/array-slice.js';

export default {
  name: "BulletinBoard",
  components: {
    LoadingSpinner,
    NewsCard
  },
  data () {
    return {
      loading: false,
      items: []
    }
  },
  computed: {
    displayItems() {
      return new ArraySlice(this.items, 0, 5);
    }
  },
  async created() {
    this.loading = true;
    this.items = await NewsService.getAllArticles();
    this.loading = false;
  }
};
</script>

<style lang="scss" scoped>
.bulletin-board {
  width: 500px;
  border-top: 5px solid #353535;

  .loading-spinner {
    min-height: 535px;
  }

  .header {
    padding: 2rem 1.5rem;
  }

  .no-news-message {
    height: 50px;
  }
}
</style>
