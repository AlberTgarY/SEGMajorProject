<template>
  <div id="page-viewer">
    <router-link to="all-pages" class="button is-light view-pages-button is-hidden-tablet">
      <font-awesome-icon icon="stream" size="1x" style="margin-right: 12px; opacity: 0.85" />View all pages
    </router-link>

    <section class="section" style="height: 100%;">
      <transition name="fade" mode="out-in">
        <loading-spinner v-if="!pages" style="padding-top: 68px" />
        <http-status :httpStatusCode="404" v-else-if="!page" />
        <div class="custom-content-container" v-else>
          <h1 class="title">{{ page.title }}</h1>

          <div class="page-content-container">
            <rich-text-editor :editable="false" v-model="page.content" />
          </div>

          <div class="bottom-nav-buttons">
            <div class="level">
              <div class="level-left">
                <div class="level-item">
                  <!-- Previous Button-->
                  <router-link :to="previousPage.slug" v-if="previousPage">
                    <div class="button is-light is-large bottom-nav-btn">
                      <i class="material-icons" style="margin-right: 5px">arrow_back_ios</i>
                      {{ previousPage.title }}
                    </div>
                  </router-link>
                </div>
              </div>
              <div class="level-right">
                <div class="level-item">
                  <!-- Next Button -->
                  <router-link :to="nextPage.slug" v-if="nextPage">
                    <div class="button is-light is-large bottom-nav-btn">
                      {{ nextPage.title }}
                      <i
                        class="material-icons"
                        style="margin-left: 12px; margin-right: -3px;"
                      >arrow_forward_ios</i>
                    </div>
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </section>
  </div>
</template>

<style lang="scss" scoped>
@import "@/variables";

#page-viewer {
  height: 100%;
}

.custom-content-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.page-content-container {
  flex-grow: 1;
}

.title {
  color: $primary-dark;
  font-size: 35px;
}

.bottom-nav-buttons {
  padding: 50px 0;
}

.bottom-nav-btn {
  transition: background-color 0.12s, color 0.12s;
  font-size: 20px;
  i {
    font-size: 18px;
  }
  &:hover {
    color: #222;
  }
}

.view-pages-button {
  margin: 18px;
  margin-bottom: 0;
}
</style>

<script>
import RichTextEditor from "@/components/RichTextEditor";
import HttpStatus from "@/components/HttpStatus";
import LoadingSpinner from "@/components/LoadingSpinner";

export default {
  components: {
    RichTextEditor,
    HttpStatus,
    LoadingSpinner
  },
  props: {
    pages: {
      type: Array
    }
  },
  metaInfo() {
    return {
      title: this.pages
              ? (this.page?.title ?? 'Page Not Found')
              : '' 
    }
  },
  computed: {
    page() {
      if (!this.pages) return null;

      const pageSlug = this.$route.params.pageSlug;
      return this.pages.find(p => p.slug === pageSlug);
    },
    nextPage() {
      const currentIndex = this.pages.indexOf(this.page);
      if (currentIndex < this.pages.length) {
        return this.pages[currentIndex + 1];
      }
      return null;
    },
    previousPage() {
      const currentIndex = this.pages.indexOf(this.page);
      if (currentIndex > 0) {
        return this.pages[currentIndex - 1];
      }
      return null;
    }
  }
};
</script>
