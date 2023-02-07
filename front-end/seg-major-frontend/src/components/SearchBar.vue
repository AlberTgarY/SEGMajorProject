<template>
  <div class="searchbox-container"
        @focusin="searchFocusGained"
        @focusout="searchFocusLost"
        ref="search">
    <div class="control has-icons-left" style="height: 100%">
      <span class="icon is-small is-left" style="height: 100%;">
        <i class="search-icon material-icons">search</i>
      </span>
      <input
        class="searchbox input"
        type="text"
        :disabled="pages === null"
        v-model="searchQuery"
        @keyup.enter="searchBoxSubmit"
        placeholder="Search"
      />

      <transition name="fade" mode="out-in">
        <div v-if="displaySearchResults" class="card search-suggestions">
          <router-link v-for="page in searchResults"
                        :key="page.slug"
                        :to="`/${page.site.slug}/${page.slug}`"
                        @click.native="displaySearchResults = false">
            <div class="card suggestion-item">{{ page.title }}</div>
          </router-link>
          <div v-if="searchResults.length == 0" class="card suggestion-item">
            <p><i>No pages matched your search</i></p>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
import SearchService from '@/services/search-service';
import ArraySlice from '@/array-slice.js';

export default {
  props: {
    pages: {
      validator: function (value) {
        return value === null || typeof value[Symbol.iterator] === 'function';
      },
      required: true,
    }
  },
  data() {
    return {
      searchQuery: '',
      fullSearchResults: [],
      searchResults: [],
      displaySearchResults: false,
    }
  },
  methods: {
    searchFocusGained() {
      if (this.searchResults.length > 0 || this.searchQuery.length >= 3) this.displaySearchResults = true;
    },
    searchFocusLost(e) {
      // Check if focus has moved somewhere else inside the search area first
      if (this.$refs.search.contains(e.relatedTarget)) return;
      this.displaySearchResults = false
    },
    searchBoxSubmit() {
      if (!this.displaySearchResults && this.searchQuery.length > 0) {
        this.doSearch(this.searchQuery);
      }
    },
    doSearch(query, oldQuery) {
      // Reuse existing search results if new query only appends to existing query
      const searchSpace = this.searchResults.length > 0 && oldQuery && query.startsWith(oldQuery)
                          ? this.fullSearchResults
                          : this.pages;
      this.fullSearchResults = SearchService.search(searchSpace, query);
      this.searchResults = new ArraySlice(this.fullSearchResults, 0, 6);
      this.displaySearchResults = true;
    },
  },
  watch: {
    searchQuery: function(newQuery, oldQuery) {
      if (newQuery.length < 3) {
        this.displaySearchResults = false;
        this.fullSearchResults = this.searchResults = [];
      } else {
        this.doSearch(newQuery, oldQuery);
      }
    },
  }
}
</script>

<style lang="scss" scoped>
.searchbox-container {
  height: 100%;
  width: 100%;
}

.input.searchbox {
  height: 100%;
  width: 100%;
  border: 0;
  box-shadow: none;
  background-color: #fbfbfb;

  &:hover {
    background-color: #f9f9f9;
  }

  &:focus {
    background-color: #f5f5f7;
  }

  &::placeholder {
    opacity: 1;
    color: #888;
  }
}

.search-icon {
  color: #aaa;
}

.search-suggestions {
  background: white;

  .suggestion-item {
    padding: 15px 20px;
  }
}
</style>