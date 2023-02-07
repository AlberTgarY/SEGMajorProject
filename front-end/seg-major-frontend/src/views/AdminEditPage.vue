<template>
  <div id="admin-edit-page">
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
                <router-link to="/admin/sites">Sites</router-link>
              </li>
              <li>
                <router-link :to="`/admin/sites/${page.site.slug}`">{{ page.site.name || '' }}</router-link>
              </li>
              <li>
                <router-link :to="`/admin/sites/${page.site.slug}/pages`">Pages</router-link>
              </li>
              <li class="is-active">
                <a href="#" aria-current="page">Page Editor</a>
              </li>
            </ul>
          </nav>

          <div class="field">
            <div class="control">
              <input
                type="text"
                class="input title"
                v-model="page.title"
                placeholder="Enter page title here..."
                :disabled="saving"
                @change="$v.page.title.$touch()"
              />
            </div>
            <div v-if="$v.page.title.$dirty">
              <p class="help is-danger" v-if="!$v.page.title.required">This field is required</p>
            </div>
          </div>

          <label class="label">URL Slug</label>
          <div class="field has-addons is-marginless">
            <p class="control">
              <a
                class="button is-static"
                v-if="page.site"
                :class="{ 'border-none': saving}"
              >/{{ page.site.slug }}/</a>
            </p>
            <p class="control is-expanded">
              <input
                class="input"
                type="text"
                v-model="page.slug"
                placeholder="Enter URL Slug here..."
                :disabled="saving"
                @input="onSlugChanged()"
              />
            </p>
          </div>
          <div v-if="$v.page.slug.$dirty">
            <p class="help is-danger" v-if="!$v.page.slug.required">This field is required</p>
            <p
              class="help is-danger"
              v-else-if="!$v.page.slug.slug"
            >Slug can only contain lowercase letters, numbers, and hyphens</p>
            <p
              class="help is-danger"
              v-else-if="!$v.page.slug.pageSlug"
            >This slug is not allowed because it is reserved</p>
            <p
              class="help is-danger"
              v-else-if="slugAlreadyExists"
            >This slug is already in use by another page</p>
          </div>
          <div style="flex-grow: 1; margin-top: 25px;">
            <rich-text-editor v-model="page.content" :disabled="saving" />
          </div>

          <div class="buttons" style="justify-content: flex-end; margin-bottom: 7px;">
            <router-link to="../" class="button is-light">Cancel</router-link>
            <button class="button is-danger" @click="deletePage()">Delete</button>
            <button
              class="button is-success"
              :class="{ 'is-loading': saving }"
              @click="save()"
              :disabled="$v.$anyError || !page.title || !page.slug"
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

#admin-edit-page {
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
import SitesService from "@/services/sites-service";
import LoadingSpinner from "@/components/LoadingSpinner";
import { required } from "vuelidate/lib/validators";
import { slug, pageSlug } from "@/custom-validators";

export default {
  components: {
    RichTextEditor,
    LoadingSpinner
  },
  props: {
    newPage: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      pagesInSite: [],
      page: {},
      loading: true,
      saving: false,
      saved: false,
      slugAlreadyExists: false
    };
  },
  metaInfo() {
    return {
      title: this.newPage ? 'New Page' : 'Edit Page'
    }
  },
  validations: {
    page: {
      title: {
        required
      },
      slug: {
        required,
        slug,
        pageSlug
      }
    }
  },
  methods: {
    async save() {
      this.$v.$touch();
      if (this.$v.$invalid) {
        return;
      }
      
      // Check if page slug conflicts with an existing page
      const existingPageSlugs = this.pagesInSite
        .filter(p => p.primaryKey !== this.page.primaryKey)
        .map(p => p.slug);
      if (existingPageSlugs.includes(this.page.slug)) {
        this.slugAlreadyExists = true;
        return;
      }

      this.saving = true;
      this.saved = false;

      if (this.newPage === true) {
        await this.createNewPage();
      } else {
        await this.updatePage();
      }

      this.saving = false;
      this.saved = true;
    },
    onSlugChanged() {
      this.$v.page.slug.$touch();
      this.slugAlreadyExists = false;
    },
    async createNewPage() {
      // Generate Page index
      const currentPages = await SitesService.getAllPages(this.page.site.slug);
      if (currentPages && currentPages.length > 0) {
        this.page.index = currentPages[currentPages.length - 1].index + 1;
      } else {
        this.page.index = 0;
      }

      const res = await SitesService.createPage(this.page);
      this.page = res;
      this.$router.push(
        `/admin/sites/${this.page.site.slug}/pages/${this.page.slug}`
      );
    },
    async updatePage() {
      await SitesService.updatePage(this.page);

      const currentSlug = this.$route.params.pageSlug;
      if (currentSlug != this.page.slug) {
        this.$router.push(
          `/admin/sites/${this.page.site.slug}/pages/${this.page.slug}`
        );
      }
    },
    async deletePage() {
      this.loading = true;
      await SitesService.deletePage(this.page);
      this.$router.push(`/admin/sites/${this.page.site.slug}/pages`);
    }
  },
  watch: {
    page: {
      handler() {
        this.saved = false;
      },
      deep: true
    }
  },
  async mounted() {
    const siteSlug = this.$route.params.siteSlug;
    const pageSlug = this.$route.params.pageSlug;

    this.pagesInSite = await SitesService.getAllPages(siteSlug);

    if (!this.newPage) {
      this.page = await SitesService.getPage(siteSlug, pageSlug);
    } else {
      this.page.site = { slug: siteSlug };
      this.page.site = await SitesService.getSite(siteSlug);
    }

    this.loading = false;
  }
};
</script>