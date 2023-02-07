import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Print from "vue-print-nb"
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faCircleNotch, faStream, faBars, faTimes, faEye, faEyeSlash, faPrint, faExclamationTriangle, faThumbtack } from '@fortawesome/free-solid-svg-icons'
import { faFrown, faCheckCircle } from '@fortawesome/free-regular-svg-icons'
import Vuelidate from 'vuelidate'

Vue.config.productionTip = false

library.add(faCircleNotch, faFrown, faStream, faBars, faCheckCircle, faTimes, faEye, faEyeSlash, faPrint, faExclamationTriangle, faThumbtack)

Vue.component('font-awesome-icon', FontAwesomeIcon)
Vue.use(Vuelidate)
Vue.use(Print)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
