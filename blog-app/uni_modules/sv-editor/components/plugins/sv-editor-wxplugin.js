import { noAuthorization } from "../common/utils"

export function wxCreateCoverThumbnail(url) {
  noAuthorization('wxCreateCoverThumbnail')
  return url
}

export default {
  wxCreateCoverThumbnail
}