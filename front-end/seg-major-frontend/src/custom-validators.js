import { helpers } from 'vuelidate/lib/validators'

export const slug = helpers.regex('slug', /^[a-z0-9-]*$/);

export const pageSlug = (value) => {
  const reservedPageSlugs = ['new', 'all-pages'];
  return !reservedPageSlugs.includes(value);
}

export const siteSlug = (value) => {
  const reservedSiteSlugs = ['new', 'news', 'drug-chart', 'password-reset', 'admin', 'login'];
  return !reservedSiteSlugs.includes(value);
}

export const articleSlug = (value) => {
  const reservedArticleSlugs = ['new'];
  return !reservedArticleSlugs.includes(value);
}
