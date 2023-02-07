<template>
  <div id="admin-home">
    <section class="hero is-light">
      <div class="hero-body">
        <div class="container">
          <h1 class="title is-2">Hi {{ username }}</h1>
          <h2 class="subtitle is-4">Welcome to the admin panel</h2>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  props: {
    username: {
      type: String,
      required: true
    }
  },
  metaInfo: {
    title: 'Home'
  },
};
</script>

<style>
</style>