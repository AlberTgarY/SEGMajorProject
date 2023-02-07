<template>
  <div id="admin-create-new-user">
    <section class="hero is-primary">
      <div class="hero-body">
        <div class="custom-content-container">
          <h1 class="title">Create New User</h1>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="custom-content-container">
        <div class="field">
          <label class="label">Name</label>
          <div class="control">
            <input
              class="input"
              v-model="name"
              @change="$v.name.$touch()"
              type="text"
              placeholder="Enter name here..."
            />
          </div>
          <div v-if="$v.name.$dirty">
            <p class="help is-danger" v-if="!$v.name.required">This field is required</p>
          </div>
        </div>

        <div class="field">
          <label class="label">Email</label>
          <div class="control">
            <input
              class="input"
              v-model="email"
              @change="$v.email.$touch()"
              type="text"
              placeholder="Enter email here ..."
            />
          </div>
          <div v-if="$v.email.$dirty">
            <p class="help is-danger" v-if="!$v.email.required">This field is required</p>
            <p class="help is-danger" v-if="!$v.email.email">Please enter a valid email</p>
          </div>
        </div>

        <div class="field">
          <label class="label">Password</label>
          <div class="control">
            <input
              class="input"
              type="password"
              v-model="password"
              placeholder="Enter password..."
              @change="$v.password.$touch()"
              @keyup.enter="createAccount()"
            />
          </div>
          <div v-if="$v.password.$dirty">
            <p class="help is-danger" v-if="!$v.password.required">This field is required</p>
            <p class="help is-danger" v-if="!$v.password.minLength">Password too short</p>
          </div>
        </div>

        <div class="level">
          <div class="level-left">
            <router-link to="/admin/settings" class="button is-light">Cancel</router-link>
          </div>
          <div class="level-right">
            <button
              class="button is-success"
              :disabled="$v.$anyError || !name || !email || !password"
              @click="createAccount()"
            >Create user</button>
          </div>
        </div>

        <div class="notification is-danger" v-if="error">
          <p>There was an error while trying to create the account.</p>
          <p>Please re-enter user details and try again.</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import UserService from "@/services/user-service";
import { required, email, minLength } from "vuelidate/lib/validators";

export default {
  data() {
    return {
      name: "",
      email: "",
      password: "",
      error: false
    };
  },
  metaInfo: {
    title: 'Create New User'
  },
  validations: {
    name: {
      required
    },
    email: {
      required,
      email
    },
    password: {
      required,
      minLength: minLength(5)
    }
  },
  methods: {
    async createAccount() {
      this.$v.$touch();
      if (this.$v.$invalid) {
        return;
      }

      try {
        await UserService.createUser({
          name: this.name,
          email: this.email,
          password: this.password
        });
        alert("Account created");
        this.$router.push("/admin/settings");
      } catch {
        this.error = true;
      }
    }
  }
};
</script>

<style lang="scss" scoped>
</style>