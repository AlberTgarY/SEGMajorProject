<template>
  <div id="site-content-viewer-layout">
    <Navbar :pages="pages" :showSearchBar="true" />
    <section class="section" v-if="site === null">
      <http-status :httpStatusCode="404" />
    </section>
    <div class="flex-wrapper" v-else>
      <div class="sidebar is-hidden-mobile">
        <div class="section">
          <div class="container">
            <p class="sidebar-label">{{ site.name }}</p>
            <nav class="sidebar-navigation">
              <transition name="fade" mode="out-in">
                <span v-if="loading">
                  <loading-spinner style="margin-top: 50px; opacity: 0.4" />
                </span>
                <div class="navigation-items" v-else>
                  <router-link
                    :to="`/${page.site.slug}/${page.slug}`"
                    v-for="page of pages"
                    :key="page.primaryKey"
                    class="navigation-item is-unselectable"
                  >{{ page.title }}</router-link>
                </div>
              </transition>
            </nav>
          </div>
        </div>
      </div>

      <div class="page-content">
        <div class="searchBarContainer is-hidden-desktop">
          <search-bar :pages="pages" />
        </div>
        <transition name="fade" mode="out-in">
          <router-view :pages="pages" :key="$route.path" />
        </transition>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
#site-content-viewer-layout {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.flex-wrapper {
  height: 100%;
  display: flex;
  overflow: hidden;
}

.page-content {
  flex-grow: 1;
  overflow-y: scroll;
}

.sidebar {
  background: #fdfdfd;
  border: none;
}

.searchBarContainer {
  height: 50px;
}
</style>

<script>
import SitesService from "@/services/sites-service";
import AppInfoService from "@/services/app-info-service";
import Navbar from "@/components/Navbar.vue";
import SearchBar from '@/components/SearchBar.vue';
import LoadingSpinner from "@/components/LoadingSpinner";
import HttpStatus from "@/components/HttpStatus";

export default {
  components: {
    Navbar,
    SearchBar,
    LoadingSpinner,
    HttpStatus,
  },
  data() {
    return {
      site: { name: '' },
      pages: null,
      loading: true,
      appInfo: {
        departmentName: ''
      },
    };
  },
  metaInfo() {
    const site = this.site;
    const appInfo = this.appInfo;
    return {
      titleTemplate: titleChunk => {
        if (site === null) {
          return `Page Not Found | ${appInfo.departmentName}`;
        } else if (titleChunk) {
          return `${titleChunk} - ${site.name} | ${appInfo.departmentName}`;
        } else {
          return `${site.name} | ${appInfo.departmentName}`;
        }
      }
    }
  },
  async created() {
    const siteSlug = this.$route.params.siteSlug;

    this.appInfo = await AppInfoService.getAppInfo();
    try {
      this.site = await SitesService.getSite(siteSlug);
    } catch (error) {
      if (error.response.status === 404) {
        this.site = null;
      } else {
        throw(error);
      }
    }
    if (this.site !== null) {
      this.pages = await SitesService.getAllPages(siteSlug);
    }

    this.loading = false;
  }
};
</script>