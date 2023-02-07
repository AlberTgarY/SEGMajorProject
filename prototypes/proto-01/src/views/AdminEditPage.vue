<template>
  <div id="root">
    <section class="section">
      <div class="custom-content-container">
        <nav class="breadcrumb is-right" aria-label="breadcrumbs">
          <ul>
            <li>
              <a href="#">Sites</a>
            </li>
            <li>
              <a href="#">Biliary Atresia</a>
            </li>
            <li>
              <a href="#">Pages</a>
            </li>
            <li class="is-active">
              <a href="#" aria-current="page">Page Editor</a>
            </li>
          </ul>
        </nav>

        <!-- <h1 class="title">// Insert page name here</h1> -->
        <h1 class="title">{{ $route.params.id }}</h1>

        <div class="notification">
          <p>
            <b>NOTE:</b>
            The page ID is: {{ $route.params.id }}
          </p>
        </div>

        <p>Insert Rich Text Editor here</p>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
</style>

<script>
export default {
  components: {}
};
</script>
