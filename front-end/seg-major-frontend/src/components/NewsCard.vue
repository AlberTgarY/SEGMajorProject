<template>
  <div class="card news-card" :class="{ urgent: newsItem.urgent }">
    <div class="card-content">
      <div class="columns is-gapless is-multiline is-mobile">
        <div class="column title-column is-full-mobile"><h1 class="item-title">{{ newsItem.title }}</h1></div>
        <div class="column is-narrow" v-if="newsItem.urgent"><div class="icon has-text-grey"><font-awesome-icon icon="exclamation-triangle" /></div></div>
        <div class="column is-narrow" v-else-if="newsItem.pinned"><div class="icon has-text-grey"><font-awesome-icon icon="thumbtack" /></div></div>
        <div class="column is-narrow"><h3 class="item-date">{{ newsItem.date.toLocaleDateString("en-GB") }}</h3></div>
      </div>
      <p class="description">{{ newsItem.description }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "NewsCard",
  props: {
    newsItem: {
      type: Object,
      required: true
    }
  }
};
</script>

<style lang="scss" scoped>
.news-card {
  transition: background-color 0.2s;

  &:hover {
    background-color: #f8f8f8;
  }

  &.urgent {
    background-color: #fff5e3;

    &:hover {
      background-color: #f8eedc;
    }
  }

  .columns {
    margin-bottom: 0.5rem !important;

    .title-column {
      overflow: hidden;

      .item-title {
        font-size: 18px;
        font-weight: bold;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .icon {
      margin-right: 0.2rem;
    }

    .item-date {
      font-size: 16px;
      opacity: 0.9;
    }
  }

  .description {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
