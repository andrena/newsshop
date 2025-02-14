<template>
  <div class="error-page">
    <h2>Subscription Error</h2>
    <p>There was an error subscribing: {{ errorMessage }}</p>
  </div>
</template>

<script>
export default {
  name: 'ErrorPage',
  props: {
    errorMessage: {
      type: String,
      required: true
    }
  }
}
</script>

<style scoped>
.error-page {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}
</style>
