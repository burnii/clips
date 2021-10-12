import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore

// prettier-ignore
const Clip = () => import('@/entities/clip/clip.vue');
// prettier-ignore
const ClipUpdate = () => import('@/entities/clip/clip-update.vue');
// prettier-ignore
const ClipDetails = () => import('@/entities/clip/clip-details.vue');
// prettier-ignore
const ClipUser = () => import('@/entities/clip-user/clip-user.vue');
// prettier-ignore
const ClipUserUpdate = () => import('@/entities/clip-user/clip-user-update.vue');
// prettier-ignore
const ClipUserDetails = () => import('@/entities/clip-user/clip-user-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/clip',
    name: 'Clip',
    component: Clip,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/clip/new',
    name: 'ClipCreate',
    component: ClipUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/clip/:clipId/edit',
    name: 'ClipEdit',
    component: ClipUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/clip/:clipId/view',
    name: 'ClipView',
    component: ClipDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/clip-user',
    name: 'ClipUser',
    component: ClipUser,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/clip-user/new',
    name: 'ClipUserCreate',
    component: ClipUserUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/clip-user/:clipUserId/edit',
    name: 'ClipUserEdit',
    component: ClipUserUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/clip-user/:clipUserId/view',
    name: 'ClipUserView',
    component: ClipUserDetails,
    meta: { authorities: [Authority.USER] },
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
