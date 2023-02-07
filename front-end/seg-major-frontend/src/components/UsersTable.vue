<template>
  <div>
    <table v-if="users" class="table is-fullwidth has-background-light">
      <thead>
        <tr>
          <th>Name</th>
          <th>Email</th>
        </tr>
      </thead>
      <tr v-for="user of users" :key="user.primaryKey">
        <td>{{ user.name }}</td>
        <td>{{ user.email }}</td>
      </tr>
    </table>
  </div>
</template>

<script>
import UserService from "@/services/user-service";

export default {
  data() {
    return {
      users: null
    };
  },
  async mounted() {
    this.users = await UserService.getAllUsers();
  }
};
</script>

<style lang="scss" scoped>
</style>