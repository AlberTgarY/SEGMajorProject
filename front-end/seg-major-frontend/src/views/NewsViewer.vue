<template>
  <div id="news-viewer">
    <Navbar :showUrgentNews="false" />
    <section class="section">
      <div class="custom-content-container">
        <loading-spinner class="loading-spinner" v-if="loading" />
        <http-status :httpStatusCode="404" v-else-if="news === null" />
        <article v-else>
          <div class="header">
            <div class="level is-mobile">
              <div class="level-left">
                <h3 class="subtitle has-text-weight-bold is-uppercase">News</h3>
              </div>
              <div class="level-right">
                <time class="subtitle has-text-grey" :datetime="news.date.toISOString().substring(0, 10)" pubdate>{{ news.date.toLocaleDateString("en-GB") }}</time>
              </div>
            </div>
            <h1 class="title">{{ news.title }}</h1>
            <h2 class="subtitle">{{ news.description }}</h2>
          </div>
          <hr>
          <rich-text-editor v-model="news.content" :editable="false" />
        </article>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
.header {
  .level {
    margin-bottom: 1rem;
  }
}

hr {
  width: 25%;
}
</style>

<script>
import Navbar from "@/components/Navbar";
import LoadingSpinner from "@/components/LoadingSpinner";
import HttpStatus from "@/components/HttpStatus";
import RichTextEditor from "@/components/RichTextEditor";
import NewsService from "@/services/news-service";

export default {
  components: {
    Navbar,
    LoadingSpinner,
    HttpStatus,
    RichTextEditor
  },
  data() {
    return {
      loading: false,
      news: {
        title: 'Loading...'
      }
    }
  },
  metaInfo() {
    return {
      title: this.news === null
        ? 'Page Not Found - News'
        : `${this.news.title} - News`
    }
  },
  async created() {
    this.loading = true;
    const slug = this.$route.params.articleSlug;
    this.news = await NewsService.getArticle(slug);
    this.loading = false;
  }
};
</script>
