<template>
  <div>
    <nav class="navbar" role="navigation" aria-label="main navigation">
      <div class="navbar-brand">
        <router-link to="/" class="navbar-item">
          <NHS-logo class="nhs-logo" />
          <div class="brand-text">
            <h1 class="brand-top">{{ appInfo.hospitalName }}</h1>
            <h1 class="brand-bottom">{{ appInfo.departmentName }}</h1>
          </div>
        </router-link>

        <div class="navbar-burger is-hidden-desktop" @click="mobileNavActive=!mobileNavActive">
          <font-awesome-icon icon="bars" size="2x" />
        </div>
      </div>

      <transition name="fade" mode="out-in">
        <div class="navbar-menu" ref="navMenu" :class="{ 'is-active': mobileNavActive }" :key="mobileNavActive">
          <div class="navbar-start">
            <router-link to="/" class="navbar-item">Home</router-link>
            <router-link to="/news" class="navbar-item">News</router-link>
            <router-link to="/drug-chart" class="navbar-item">Drug Chart</router-link>
          </div>

          <div class="navbar-end search is-hidden-touch">
            <search-bar v-if="showSearchBar" :pages="pages" />
          </div>
        </div>
      </transition>
    </nav>
    <transition name="vertical-slide" v-if="displayUrgentNews" appear>
      <router-link class="link" :to="`/news/${urgentNews.slug}`">
        <div class="notification is-warning urgent-news">
          <button class="delete" @click.prevent="closeUrgentNews"></button>
          <strong>{{ urgentNews.title }}:</strong> {{ urgentNews.description }}
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script>
import NewsService from '@/services/news-service';
import AppInfoService from "@/services/app-info-service";
import SearchBar from '@/components/SearchBar.vue';
import NHSLogo from "@/components/NHSLogo";

export default {
  name: "Navbar",
  props: {
    showSearchBar: {
      type: Boolean,
      default: false
    },
    showUrgentNews: {
      type: Boolean,
      default: true
    },
    pages: {
      type: Array
    }
  },
  components: {
    SearchBar,
    NHSLogo,
  },
  computed: {
    displayUrgentNews() {
      return this.showUrgentNews
          && this.urgentNews
          && !this.localHiddenState
          && !this.getHiddenState();
    },
  },
  methods: {
    closeUrgentNews() {
      localStorage.setItem('hide-urgent-news', JSON.stringify(this.urgentNews.slug));
      this.localHiddenState = true;
    },
    getHiddenState() {
      const savedState = localStorage.getItem('hide-urgent-news');
      return savedState && JSON.parse(savedState) === this.urgentNews.slug;
    },
  },
  data () {
    return {
      urgentNews: undefined,
      localHiddenState: false,
      mobileNavActive: false,
      appInfo: {
        hospitalName: '',
        departmentName: '',
        contactDetails: ''
      },
    }
  },
  async created() {
    this.appInfo = await AppInfoService.getAppInfo();
    const items = await NewsService.getAllArticles();
    if (items.length > 0) {
      const firstItem = items[0];
      if (firstItem.urgent) this.urgentNews = firstItem;
    }
  },
};
</script>

<style lang="scss" scoped>
@import "~bulma/sass/utilities/functions";          // \
@import "~bulma/sass/utilities/initial-variables";  //  | Remove after Bulma issue #2773 fixed
@import "~bulma/sass/utilities/derived-variables";  // /
@import "~bulma/sass/utilities/mixins";

.vertical-slide-leave, .vertical-slide-enter-to {
  top: 0;
}
.vertical-slide-enter-active, .vertical-slide-leave-active {
  position: relative;
  transition: top 0.5s;
}
.vertical-slide-enter, .vertical-slide-leave-to {
  top: -40px;
}

.notification.urgent-news {
  transition: background-color 0.2s;
  background-color: #ffe374;
  border-radius: 0;
  padding: 0.4rem 2.75rem 0.4rem 1.5rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  link {
    text-decoration: none;
  }

  .delete {
    transition: opacity 0.2s;
    opacity: 0.5;
    right: 0.75rem;
  }

  &:hover {
    background-color: #ffda47;

    .delete {
      opacity: 1;
    }
  }
}

nav.navbar {
  flex-shrink: 0;
  height: 68px;
  border-bottom: 3px solid #f5f5f8;

  .navbar-brand {
    padding-left: 14px;
    height: 100%;

    @include mobile {
      padding-left: 0;
    }

    .nhs-logo {
      height: 35px;
      margin-right: 24px;

      @include mobile {
        margin-right: 12px;
      }
    }
  }

  .navbar-burger {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 68px;
  }

  .brand-text {
    .brand-top {
      font-size: 13.5px;
      opacity: 0.85;
      margin-bottom: -2px;
    }
    .brand-bottom {
      font-weight: bold;
      font-size: 16px;
      opacity: 0.92;
      margin-bottom: 0px;
    }
  }
  .search {
    width: 400px;
  }
}
</style>
