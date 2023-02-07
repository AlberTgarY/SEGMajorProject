<template>
  <div id="login">
    <div id="bg"></div>
    <Navbar />
    <div class="custom-content-container">
      <div class="card login-box">
        <div class="notification" v-if="sessionExpired">
          <strong>Session has expired.</strong> Please re-login.
        </div>
        <h1 class="title">Login</h1>

        <div class="field">
          <label class="label">Email</label>
          <div class="control">
            <input
              class="input"
              v-model="email"
              @change="$v.email.$touch()"
              type="text"
              placeholder="Enter email here..."
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
            <div class="field has-addons">
              <div class="control is-expanded">
                <input
                  class="input"
                  v-model="password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="Enter password here..."
                  @change="$v.password.$touch()"
                  @keyup.enter="login()"
                />
              </div>
              <div class="control">
                <button 
                  class="button"
                  @click="showPassword = !showPassword" 
                >
                  <font-awesome-icon :icon="showPassword ? 'eye-slash' : 'eye'" />
                </button>
              </div>
            </div>
          </div>
          <div v-if="$v.password.$dirty">
            <p class="help is-danger" v-if="!$v.password.required">This field is required</p>
            <p class="help is-danger" v-if="!$v.password.minLength">Password too short</p>
          </div>
        </div>
        <div class="notification is-danger" v-if="loginError">
          <strong>Error:</strong> Please re-enter your credentials and try again.
        </div>
        <button
          class="button is-primary is-medium login-button"
          :disabled="$v.$anyError || !email || !password"
          @click="login()"
        >Login</button>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue";
import UserService from "@/services/user-service";
import { required, minLength, email } from "vuelidate/lib/validators";

export default {
  components: {
    Navbar
  },
  props: {
    sessionExpired: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      email: "",
      password: "",
      loginError: false,
      showPassword: false
    };
  },
  metaInfo: {
    title: 'Login - Admin',
    meta: [{ vmid: 'robots', name: 'robots', content: 'noindex' }]
  },
  validations: {
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
    async login() {
      this.$v.$touch();
      if (this.$v.$invalid) {
        return;
      }
      
      try {
        await UserService.login(this.email, this.password);
        this.$router.push("/admin");
      } catch {
        this.loginError = true;
      }
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/variables";

#login {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: auto;

  #bg {
    z-index: -1;
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: rgb(52, 243, 167);
    background-image: linear-gradient(
        45deg,
        rgb(55, 255, 175) 25%,
        transparent 25%,
        transparent 75%,
        rgb(37, 247, 163) 75%,
        rgb(82, 247, 181)
      ),
      linear-gradient(
        45deg,
        rgb(43, 238, 160) 25%,
        transparent 25%,
        transparent 75%,
        rgb(62, 250, 175) 75%,
        rgb(55, 255, 175)
      );
    background-size: 200px 200px;
    background-position: 0 0, 100px 100px;
    filter: blur(1px);
  }

  .login-button {
    margin-top: 10px;
  }
}

.custom-content-container {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  justify-content: center;

  width: 100%;
  max-width: 500px;
}

.login-box {
  padding: 50px;
  margin-bottom: 20px;
  border-top: 5px solid #353535;
}
</style>
