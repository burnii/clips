import { Component, Inject } from 'vue-property-decorator';

import { mixins } from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';

import { IClip } from '@/shared/model/clip.model';
import ClipService from './clip.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class ClipDetails extends mixins(JhiDataUtils) {
  @Inject('clipService') private clipService: () => ClipService;
  @Inject('alertService') private alertService: () => AlertService;

  public clip: IClip = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.clipId) {
        vm.retrieveClip(to.params.clipId);
      }
    });
  }

  public retrieveClip(clipId) {
    this.clipService()
      .find(clipId)
      .then(res => {
        this.clip = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
