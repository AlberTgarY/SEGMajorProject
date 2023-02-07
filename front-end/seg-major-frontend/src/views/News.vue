<template>
  <div id="news-root">
    <Navbar :showUrgentNews="false" />
    <section class="hero is-primary">
      <div class="hero-body">
        <div class="container">
          <h1 class="title">News</h1>
          <h2 class="subtitle">Read our latest news and guidance</h2>
        </div>
      </div>
    </section>

    <div class="section">
      <div class="container">
        <transition name="fade" mode="out-in">
          <loading-spinner v-if="loading" class="loading-spinner" />
          <div v-else-if="paginatedItems.length === 0" class="has-text-dark has-text-centered">
            <div style="margin-bottom: 1rem;"><font-awesome-icon :icon="['far', 'frown']" size="3x" /></div>
            There are no news items at this time
          </div>
          <div v-else>
            <transition name="fade" mode="out-in">
              <div :key="currentPageNumber">
                <router-link v-for="item in paginatedItems" :key="item.slug" :to="item.slug" append>
                  <news-card :newsItem="item" class="news-card" />
                </router-link>
              </div>
            </transition>
            <nav class="pagination is-centered" role="navigation" aria-label="pagination">
              <a class="pagination-previous" @click="navigatePrevious" :disabled="!hasPreviousPage">Newer</a>
              <a class="pagination-next" @click="navigateNext" :disabled="!hasNextPage">Older</a>
              <ul class="pagination-list">
                <li>
                  <router-link to="news"
                                class="pagination-link"
                                v-bind="currentPageNumber === 1 ? { 'aria-label': 'Page 1', 'aria-current': 'page', 'class': 'is-current' } : { 'aria-label': 'Goto page 1' }">1</router-link>
                </li>
                <li v-if="pageCount > 5 && currentPageNumber > 3"><span class="pagination-ellipsis">&hellip;</span></li>
                <li v-for="pageNumber in middlePageNumbers" :key="pageNumber">
                    <router-link :to="{ path: 'news', query: { page: pageNumber } }"
                                  class="pagination-link"
                                  v-bind="pageNumber === currentPageNumber ? { 'aria-label': `Page ${pageNumber}`, 'aria-current': 'page', 'class': 'is-current' } : { 'aria-label': `Goto page ${pageNumber}` }">{{ pageNumber }}</router-link>
                </li>
                <li v-if="pageCount > 5 && currentPageNumber < pageCount - 2"><span class="pagination-ellipsis">&hellip;</span></li>
                <li v-if="pageCount > 1">
                  <router-link :to="{ path: 'news', query: { page: pageCount } }"
                                class="pagination-link"
                                v-bind="currentPageNumber === pageCount ? { 'aria-label': `Page ${pageCount}`, 'aria-current': 'page', 'class': 'is-current' } : { 'aria-label': `Goto page ${pageCount}` }">{{ pageCount }}</router-link>
                </li>
              </ul>
            </nav>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import NewsService from '@/services/news-service';
import Navbar from "@/components/Navbar.vue";
import NewsCard from "@/components/NewsCard.vue";
import LoadingSpinner from '@/components/LoadingSpinner.vue';
import ArraySlice from '@/array-slice.js';

export default {
  props: {
    currentPageNumber: {
      type: Number,
      default: 1
    }
  },
  components: {
    Navbar,
    NewsCard,
    LoadingSpinner
  },
  metaInfo: {
    title: 'News'
  },
  methods: {
    navigatePrevious() {
      if (!this.hasPreviousPage) return;
      const newPageNumber = this.currentPageNumber - 1;
      if (newPageNumber === 1) this.$router.push('news');
      else this.$router.push({ path: 'news', query: { page: newPageNumber } });
    },
    navigateNext() {
      if (!this.hasNextPage) return;
      const newPageNumber = this.currentPageNumber + 1;
      this.$router.push({ path: 'news', query: { page: newPageNumber } });
    },
  },
  computed: {
    paginatedItems() {
      const start = (this.currentPageNumber - 1) * this.pageSize;
      return new ArraySlice(this.items, start, this.pageSize);
    },
    pageCount() {
      return Math.max(Math.ceil(this.items.length / this.pageSize), 1);
    },
    hasPreviousPage() {
      return this.currentPageNumber > 1;
    },
    hasNextPage() {
      return this.currentPageNumber < this.pageCount;
    },
    middlePageNumbers() {
      if (this.pageCount < 3) return [];

      const middleNumbersCount = Math.min(this.pageCount - 2, 3);
      let ret = [];
      if (this.currentPageNumber <= 3) {
        for (let i = 0; i < middleNumbersCount; ++i) ret.push(i + 2);
      } else if (this.currentPageNumber >= this.pageCount - 2) {
        for (let i = 0; i < middleNumbersCount; ++i) ret.unshift(this.pageCount - 1 - i);
      } else {
        ret = [this.currentPageNumber - 1, this.currentPageNumber, this.currentPageNumber + 1];
      }
      return ret;
    },
  },
  data () {
    return {
      loading: false,
      items: [],
      pageSize: 10
    }
  },
  async created() {
    this.loading = true;
    this.items = await NewsService.getAllArticles();

    let newPageNumber;
    if (this.currentPageNumber > this.pageCount) newPageNumber = Math.min(this.pageCount, this.currentPageNumber);
    else if (this.currentPageNumber < 0) newPageNumber = 1;

    if (newPageNumber === 1) this.$router.push('news');
    else if (newPageNumber) this.$router.push({ path: 'news', query: { page: newPageNumber } });

    this.loading = false;
  },
};
</script>

<style lang="scss" scoped>
  .news-card {
    margin-bottom: 1rem;
  }

  .pagination {
    margin-top: 3rem;
  }
</style>
