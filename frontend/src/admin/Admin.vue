<template>
  <div class="admin-dashboard">
    <h2>Newsletter Admin Dashboard</h2>
    <div class="search-box">
      <input type="text" v-model="searchEmail" placeholder="Search by email">
      <button @click="searchSubscribers">Search</button>
    </div>
    <table>
      <thead>
        <tr>
          <th>Email</th>
          <th>Name</th>
          <th>Source</th>
          <th>Confirmed</th>
          <th>Mail Properties</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="subscriber in subscribers" :key="subscriber.id">
          <td>{{ subscriber.email }}</td>
          <td v-html="subscriber.name"></td>
          <td v-html="subscriber.source"></td>
          <td>{{ subscriber.confirmed ? 'Yes' : 'No' }}</td>
          <td v-html="subscriber.mailProperties"></td>
          <td>
            <button @click="deleteSubscriber(subscriber.id)" class="delete-btn">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Admin',
  data() {
    return {
      subscribers: [],
      searchEmail: ''
    }
  },
  created() {
    this.loadSubscribers()
  },
  methods: {
    async loadSubscribers() {
      try {
        const response = await axios.get('/api/newsletter/subscribers')
        this.subscribers = response.data
      } catch (error) {
        alert('Error loading subscribers: ' + error.message)
      }
    },
    async searchSubscribers() {
      try {
        const response = await axios.get('/api/newsletter/search', {
          params: { email: this.searchEmail }
        })
        this.subscribers = response.data
      } catch (error) {
        alert('Error searching subscribers: ' + error.message)
      }
    },
    async deleteSubscriber(id) {
      try {
        await axios.delete(`/api/newsletter/unsubscribe/${id}`)
        await this.loadSubscribers()
      } catch (error) {
        alert('Error deleting subscriber: ' + error.message)
      }
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.search-box {
  margin-bottom: 20px;
}
.search-box input {
  padding: 8px;
  margin-right: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}
th {
  background-color: #f5f5f5;
}
.delete-btn {
  background-color: #ff4444;
  color: white;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.delete-btn:hover {
  background-color: #cc0000;
}
</style>
