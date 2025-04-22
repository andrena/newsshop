<template>
  <div v-if="showConfirmation">
    <ConfirmationPage :email="email" :name="name" :source="source" />
  </div>
  <div v-else-if="showError">
    <ErrorPage :errorMessage="errorMessage" />
  </div>
  <div v-else class="subscription-form">
    <h2>Newsletter Subscription</h2>
    <form @submit.prevent="subscribe">
      <div class="form-group">
        <label for="email">E-Mail:</label>
        <input type="email" id="email" v-model="email" required>
      </div>
      <div class="form-group">
        <label for="name">Name:</label>
        <input type="text" id="name" v-model="name" required>
      </div>
      <div class="form-group">
        <label for="source">Quelle:</label>
        <select id="source" v-model="source" required>
          <option value="Website">Website</option>
          <option value="Social Media">Social Media</option>
          <option value="Friend">Freund</option>
          <option value="Other">Andere</option>
        </select>
      </div>
      <div class="form-group" v-if="source === 'Other'">
        <label for="otherSource">Bitte geben Sie die Quelle an:</label>
        <textarea id="otherSource" v-model="otherSource" rows="3"></textarea>
      </div>
      <button type="submit">Abonnieren</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios'
import ConfirmationPage from './ConfirmationPage.vue'
import ErrorPage from './ErrorPage.vue'

export default {
  name: 'Subscribe',
  components: {
    ConfirmationPage,
    ErrorPage
  },
  data() {
    return {
      email: '',
      name: '',
      source: '',
      otherSource: '',
      showConfirmation: false,
      showError: false,
      errorMessage: ''
    }
  },
  methods: {
    async subscribe() {
      try {
        const response = await axios.post('/api/newsletter/subscribe', {
          email: this.email,
          name: this.name,
          source: this.source === 'Other' ? this.otherSource : this.source
        })
        this.showConfirmation = true
        this.email = response.data.email
        this.name = response.data.name
        this.source = response.data.source
      } catch (error) {
        this.errorMessage = `${error.message}: ${JSON.stringify(error.response?.data)}`
        this.showError = true
      }
    }
  }
}
</script>

<style scoped>
.subscription-form {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}
.form-group {
  margin-bottom: 15px;
  text-align: left;
}
label {
  display: block;
  margin-bottom: 5px;
}
input,
select,
textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
button {
  background-color: #42b983;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #3aa876;
}
</style>
