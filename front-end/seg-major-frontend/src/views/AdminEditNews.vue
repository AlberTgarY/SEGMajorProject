<template>
  <div id="admin-edit-news">
    <section class="section">
      <transition name="fade" mode="out-in">
        <loading-spinner v-if="loading" style="padding-top: 68px;" />
        <div class="custom-content-container" v-else>
          <nav class="breadcrumb is-right" aria-label="breadcrumbs">
            <ul>
              <li>
                <router-link to="/admin">Admin</router-link>
              </li>
              <li>
                <router-link to="/admin/news">News</router-link>
              </li>
              <li class="is-active">
                <a href="#" aria-current="page">News Editor</a>
              </li>
            </ul>
          </nav>

          <div class="field">
            <div class="control">
              <input
                type="text"
                class="input title"
                v-model="article.title"
                placeholder="Enter article title here..."
                :disabled="saving"
                @change="$v.article.title.$touch()"
              />
            </div>
            <div v-if="$v.article.title.$dirty">
              <p class="help is-danger" v-if="!$v.article.title.required">This field is required</p>
            </div>
          </div>

          <div class="field">
            <label class="label">Description</label>
            <div class="control">
              <input
                type="text subtitle"
                class="input"
                v-model="article.description"
                placeholder="Enter article description here..."
                :disabled="saving"
                @change="$v.article.description.$touch()"
              />
            </div>
            <div v-if="$v.article.description.$dirty">
              <p
                class="help is-danger"
                v-if="!$v.article.description.required"
              >This field is required</p>
            </div>
          </div>

          <label class="label">URL Slug</label>
          <div class="field has-addons is-marginless">
            <p class="control">
              <a class="button is-static" :class="{ 'border-none': saving}">/news/</a>
            </p>
            <p class="control is-expanded">
              <input
                class="input"
                type="text"
                v-model="article.slug"
                placeholder="Enter URL Slug here..."
                :disabled="saving"
                @input="onSlugChanged()"
              />
            </p>
          </div>
          <div v-if="$v.article.slug.$dirty">
            <p class="help is-danger" v-if="!$v.article.slug.required">This field is required</p>
            <p
              class="help is-danger"
              v-else-if="!$v.article.slug.slug"
            >Slug can only contain lowercase letters, numbers, and hyphens</p>
            <p
              class="help is-danger"
              v-else-if="!$v.article.slug.articleSlug"
            >This slug is not allowed because it is reserved</p>
            <p
              class="help is-danger"
              v-else-if="slugAlreadyExists"
            >This slug is already in use by another article</p>
          </div>

          <div class="field is-grouped" style="margin-top: 20px;">
            <div class="control">
              <label class="checkbox">
                <input type="checkbox" v-model="article.urgent" />
                <span class="icon is-small">
                  <font-awesome-icon icon="exclamation-triangle" />
                </span>Urgent
              </label>
            </div>

            <div class="control">
              <label class="checkbox" :disabled="article.urgent">
                <input type="checkbox" :disabled="article.urgent" v-model="article.pinned" />
                <span>
                  <span class="icon is-small">
                    <font-awesome-icon icon="thumbtack" />
                  </span>Pinned
                </span>
              </label>
            </div>
          </div>

          <p
            class="help is-danger"
            v-if="urgentAlreadyExists"
          >Another article is already urgent</p>
          <p
            class="help is-info"
            v-if="article.urgent"
          >Article will appear at the top of the bulletin board and news page, and will be shown as a banner at the top of all public pages.</p>
          <p
            class="help is-info"
            v-else-if="article.pinned"
          >Article will appear at the top of the bulletin board and news page.</p>

          <div style="flex-grow: 1; margin-top: 25px;">
            <rich-text-editor v-model="article.content" :disabled="saving" />
          </div>

          <div v-if="$v.article.content.$dirty">
            <p class="help is-danger" v-if="!$v.article.content.required">This field is required</p>
          </div>

          <div class="buttons" style="justify-content: flex-end; margin-bottom: 7px;">
            <router-link to="/admin/news" class="button is-light">Cancel</router-link>
            <button class="button is-danger" @click="deleteArticle()">Delete</button>
            <button
              class="button is-success"
              :class="{ 'is-loading': saving }"
              @click="save()"
              :disabled="$v.$anyError || !article.title || !article.description || !article.slug "
            >Save</button>
          </div>
          <div class="saved-notification-container">
            <transition name="fade" mode="out-in">
              <div
                class="box notification saved-notification"
                :key="saved"
                :class="{ 'is-invisible': !saved }"
              >
                <font-awesome-icon :icon="['far', 'check-circle']" class="check-icon" />Saved
              </div>
            </transition>
          </div>
        </div>
      </transition>
    </section>
  </div>
</template>

<style lang="scss" scoped>
@import "@/variables";

#admin-edit-news {
  height: 100%;
  overflow-y: scroll;
}

.section {
  height: 100%;
}

.custom-content-container {
  display: flex;
  flex-direction: column;
  height: 100%;

  .checkbox .icon {
    display: inline-block;
    margin-left: 5px;
    margin-right: 5px;
    text-align: center;
  }
}

.saved-notification-container {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 30px;

  .saved-notification {
    font-size: 18px;
    border-left: 4px solid $primary;
    padding: 7px 12px;
    border-radius: 0;

    .check-icon {
      color: $primary-dark;
      margin-right: 10px;
    }
  }
}

input,
.button.is-static {
  transition: 0.18s;
}

.border-none {
  border-color: transparent !important;
}
</style>

<script>
import RichTextEditor from "@/components/RichTextEditor";
import NewsService from "@/services/news-service";
import LoadingSpinner from "@/components/LoadingSpinner";
import { required } from "vuelidate/lib/validators";
import { slug, articleSlug } from "@/custom-validators";

export default {
  components: {
    RichTextEditor,
    LoadingSpinner
  },
  props: {
    newArticle: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      news: [],
      article: {
        title: "",
        description: "",
        slug: "",
        content: "",
        pinned: false,
        urgent: false
      },
      loading: true,
      saving: false,
      saved: false,
      slugAlreadyExists: false,
      urgentAlreadyExists: false
    };
  },
  metaInfo() {
    return {
      title: this.newArticle ? 'New Article' : 'Edit Article'
    }
  },
  validations: {
    article: {
      title: {
        required
      },
      description: {
        required
      },
      slug: {
        required,
        slug,
        articleSlug
      },
      content: {
        required
      }
    }
  },
  methods: {
    async save() {
      this.$v.$touch();
      if (this.$v.$invalid) {
        return;
      }
      
      // Check if article slug conflicts with an existing article
      const existingArticleSlugs = this.news
        .filter(a => a.primaryKey !== this.article.primaryKey)
        .map(a => a.slug);
      if (existingArticleSlugs.includes(this.article.slug)) {
        this.slugAlreadyExists = true;
        return;
      }

      if (this.article.urgent) {
        // Check if another article is already urgent
        const existingUrgentArticle = this.news.find(
          a => a.primaryKey !== this.article.primaryKey && a.urgent
        );
        if (existingUrgentArticle) {
          this.urgentAlreadyExists = true;
          return;
        }
      }
      this.saving = true;
      this.saved = false;

      if (this.newArticle === true) {
        await this.createNewArticle();
      } else {
        await this.updateArticle();
      }

      this.saving = false;
      this.saved = true;
    },
    onSlugChanged() {
      this.$v.article.slug.$touch();
      this.slugAlreadyExists = false;
    },
    async createNewArticle() {
      this.article.date = Date.now();
      const res = await NewsService.createArticle(this.article);
      this.article = res;
      this.$router.push(`/admin/news/${this.article.slug}`);
    },
    async updateArticle() {
      await NewsService.updateArticle(this.article);

      const currentSlug = this.$route.params.articleSlug;
      if (currentSlug != this.article.slug) {
        this.$router.push(`/admin/news/${this.article.slug}`);
      }
    },
    async deleteArticle() {
      this.loading = true;
      await NewsService.deleteArticle(this.article);
      this.$router.push("/admin/news");
    }
  },
  watch: {
    news: {
      handler() {
        this.saved = false;
      },
      deep: true
    }
  },
  async mounted() {
    const articleSlug = this.$route.params.articleSlug;

    this.news = await NewsService.getAllArticles();

    if (!this.newArticle) {
      this.article = await NewsService.getArticle(articleSlug);
    }

    this.loading = false;
  }
};
</script>