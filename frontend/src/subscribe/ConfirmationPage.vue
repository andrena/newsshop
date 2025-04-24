<template>
  <div class="confirmation-page">
    <h2>Subscription Successful</h2>
    <p>Thank you for subscribing to our newsletter!</p>
    <table>
      <tbody>
      <tr>
        <td>e-mail:</td>
        <td>{{email}}</td>
      </tr>
      <tr>
        <td>name:</td>
        <td v-html="name"></td>
      </tr>
      <tr>
        <td>source:</td>
        <td v-html="source"></td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: 'ConfirmationPage',
  props: {
    email: {
      type: String
    },
    name: {
      type: String
    },
    source: {
      type: String
    },
  }
}
</script>

<style scoped>
.confirmation-page {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;

  table {
    text-align: left;
  }

}
</style>
