import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IClip } from '@/shared/model/clip.model';

import JhiDataUtils from '@/shared/data/data-utils.service';

import ClipService from './clip.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class Clip extends mixins(JhiDataUtils) {
  @Inject('clipService') private clipService: () => ClipService;
  @Inject('alertService') private alertService: () => AlertService;

  private removeId: number = null;

  public clips: IClip[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllClips();
  }

  public clear(): void {
    this.retrieveAllClips();
  }

  public retrieveAllClips(): void {
    this.isFetching = true;
    this.clipService()
      .retrieve()
      .then(
        res => {
          this.clips = res.data;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          this.alertService().showHttpError(this, err.response);
        }
      );
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: IClip): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeClip(): void {
    this.clipService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('clipsApp.clip.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllClips();
        this.closeDialog();
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
