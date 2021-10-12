import { Component, Inject } from 'vue-property-decorator';

import { mixins } from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';

import { required } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import ClipUserService from '@/entities/clip-user/clip-user.service';
import { IClipUser } from '@/shared/model/clip-user.model';

import { IClip, Clip } from '@/shared/model/clip.model';
import ClipService from './clip.service';

const validations: any = {
  clip: {
    name: {
      required,
    },
    content: {
      required,
    },
    positiveCount: {},
    negativeCount: {},
  },
};

@Component({
  validations,
})
export default class ClipUpdate extends mixins(JhiDataUtils) {
  @Inject('clipService') private clipService: () => ClipService;
  @Inject('alertService') private alertService: () => AlertService;

  public clip: IClip = new Clip();

  @Inject('clipUserService') private clipUserService: () => ClipUserService;

  public clipUsers: IClipUser[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.clipId) {
        vm.retrieveClip(to.params.clipId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.clip.id) {
      this.clipService()
        .update(this.clip)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('clipsApp.clip.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.clipService()
        .create(this.clip)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('clipsApp.clip.created', { param: param.id });
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrieveClip(clipId): void {
    this.clipService()
      .find(clipId)
      .then(res => {
        this.clip = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public clearInputImage(field, fieldContentType, idInput): void {
    if (this.clip && field && fieldContentType) {
      if (Object.prototype.hasOwnProperty.call(this.clip, field)) {
        this.clip[field] = null;
      }
      if (Object.prototype.hasOwnProperty.call(this.clip, fieldContentType)) {
        this.clip[fieldContentType] = null;
      }
      if (idInput) {
        (<any>this).$refs[idInput] = null;
      }
    }
  }

  public initRelationships(): void {
    this.clipUserService()
      .retrieve()
      .then(res => {
        this.clipUsers = res.data;
      });
  }
}
