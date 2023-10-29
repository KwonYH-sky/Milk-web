const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
axios.defaults.xsrfCookieName = token;
axios.defaults.xsrfHeaderName = header;

$(document).ready(() => {
  document.getElementById("deleteBtn")?.addEventListener("click", () => {
    if (confirm("게시물를 삭제하시겠습니까?")) {
      document.getElementById("btnForm").submit();
    }
  });

  // 댓글 불러오기
  getComments(0);

  // 댓글 등록 이벤트 추가
  const commentBth = document.getElementById("comment_btn");
  commentBth?.addEventListener("click", writeComment); // 비로그인 시 null이기 때문에 옵셔널체이닝
});

const likeBoard = async () => {
  const url = "/board/like";
  const boardId = parseInt($("#boardId").val(), 10);
  const token = $("meta[name='_csrf']").attr("content");
  const header = $("meta[name='_csrf_header']").attr("content");

  await axios
    .post(
      url,
      {
        boardId: boardId,
      },
      {
        headers: {
          "Content-Type": "application/json; charset=utf-8",
          [header]: token,
        },
      }
    )
    .then((res) => {
      console.log(res);
      alert("추천 완료");
    })
    .catch((err) => {
      console.error(err);
      if (err.response) {
        // 요청이 전송되었고, 서버는 2xx 외의 상태 코드로 응답했습니다.
        console.log(err.response.data);
        console.log(err.response.status);
        console.log(err.response.headers);

        // 로그인이 아닐 때.
        if (err.response.status == 401) {
          if (confirm("로그인이 필요합니다. 로그인 하시겠습니까?")) {
            location.href = "/member/login";
          }
        } else {
          // 그 외
          alert(err.response.data.message);
        }
      } else if (err.request) {
        // 요청이 전송되었지만, 응답이 수신되지 않았습니다.
        // 'err.request'는 브라우저에서 XMLHtpRequest 인스턴스 입니다.
        console.log(err.request);
      } else {
        // 오류가 발생한 요청을 설정하는 동안 문제가 발생했습니다.
        console.log("Error", err.message);
      }
    });

  await axios
    .get(url + "/" + boardId)
    .then((res) => {
      console.log(res);
      $("#boardLikes").text(res.data);
    })
    .catch((err) => {
      console.error(err);
    });
};

const writeComment = async () => {
  const header = $("meta[name='_csrf_header']").attr("content");
  const token = $("meta[name='_csrf']").attr("content");

  const url = "/comment/write";
  const comment = $("#comment_input").val();
  const boardId = parseInt($("#boardId").val(), 10);

  await axios
    .post(
      url,
      {
        comment: comment,
        boardId: boardId,
      },
      {
        headers: {
          "Content-Type": "application/json; charset=utf-8",
          [header]: token,
        },
      }
    )
    .then((res) => {
      document.getElementById("comment_input").value = "";
      console.log(res);
    })
    .catch((err) => {
      console.error(err);

      // @Valid error 메시지
      let errMessage = "";
      for (const e of err.response.data.errors) {
        errMessage += e.defaultMessage + "\n";
      }
      alert(errMessage);
    });

  await getComments(0);
};

const getComments = async (page) => {
  const url = "/comment/board/";
  const boardId = $("#boardId").val();
  const params = {
    page: page,
  };

  axios
    .get(url + boardId, {
      params: params,
    })
    .then((res) => {
      console.log(res);
      const data = res.data;
      let comments = "";
      // 로그인한 회원의 댓글Id들
      const loginComments = [];

      for (const c of data.content) {
        comments += `
            <li id="comment_${c.id}" class="border-bottom border-dark-subtle">
              <div class="row my-3">
                <div class="fw-bold col-2">${c.memberName}</div>
                <div class="col-10">
                  ${c.comment}</div>
            `;

        // 로그인한 회원의 댓글이면 수정 & 삭제 버튼 추가 and 배열에 저장
        if (c.isUserCommentAuthor) {
          comments += `
              <div>
                <button id="uc_${c.id}" type="button" class="btn btn-outline-primary">수정</button>
                <button id="dc_${c.id}" type="button" class="btn btn-outline-danger">삭제</button>
              </div>
              `;
          // 수정할 때 넘겨줄 comment 정보
          const commentDetail = {
            id: c.id,
            member: c.memberName,
            content: c.comment,
          };
          loginComments.push(commentDetail);
        }
        comments += `
               </div>
            </li>
            `;
      }
      $("#comments").html(comments);

      // 클릭 이벤트 추가 (DOM 메소드 & JQuery)
      loginComments.map((comment) => {
        document.getElementById("uc_" + comment.id).onclick = () =>
          updateCommentForm(comment);
        $("#dc_" + comment.id).click(() => deleteComment(comment.id));
      });

      // 댓글 페이징
      let commentPage = "";
      for (let i = 0; i < data.totalPages; i++) {
        commentPage += `
            <li class="page-item"><a id="page${i}" class="page-link">${
          i + 1
        }</a></li>
            `;
      }
      $("#comment_paging").html(commentPage);

      // 페이지 클릭 이벤트 추가
      for (let i = 0; i < data.totalPages; i++) {
        $("#page" + i).click(() => getComments(i));
      }
    })
    .catch((err) => {
      console.error(err);
    });
};

const updateCommentForm = (comment) => {
  document.getElementById("commentCancel_btn")?.click();
  const prevComment = $("#comment_" + comment.id).html();

  $("#comment_" + comment.id).html(`
      <div class="row my-3">
              <div class="fw-bold col-2">${comment.member}</div>
              <div class="col-10">
                <textarea class="form-control" id="commentUpdate_input" rows="3" placeholder="댓글을 수정해주세요.">${comment.content}</textarea>
                <button type="button" id="commentUpdate_btn" class="btn btn-success my-3">수정하기</button>
                <button type="button" id="commentCancel_btn" class="btn btn-warning my-3">취소</button>
              </div>
      </div>
      `);

  $("#commentUpdate_btn").click(() => {
    const header = $("meta[name='_csrf_header']").attr("content");
    const token = $("meta[name='_csrf']").attr("content");

    const url = "/comment/update";
    const data = {
      id: comment.id,
      comment: $("#commentUpdate_input").val(),
    };
    const csrfToken = {
      headers: {
        "Content-Type": "application/json; charset=utf-8",
        [header]: token,
      },
    };

    axios
      .patch(url, data, csrfToken)
      .then((res) => {
        console.log(res);
        alert("댓글 수정 완료");
        getComments(0);
      })
      .catch((err) => {
        console.log(err);

        // @Valid error 메시지
        let errMessage = "";
        for (const e of err.response.data.errors) {
          errMessage += e.defaultMessage + "\n";
        }
        alert(errMessage);
      });
  });

  $("#commentCancel_btn").click(() => {
    $("#comment_" + comment.id).html(prevComment);

    document.getElementById("uc_" + comment.id).onclick = () =>
      updateCommentForm(comment);
    $("#dc_" + comment.id).click(() => deleteComment(comment.id));
  });
};

const deleteComment = (commentId) => {
  const header = $("meta[name='_csrf_header']").attr("content");
  const token = $("meta[name='_csrf']").attr("content");

  const url = "/comment/delete/";
  const csrfToken = {
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      [header]: token,
    },
  };

  axios
    .delete(url + commentId, csrfToken)
    .then((res) => {
      console.log(res);
      alert(res.data);

      getComments(0);
    })
    .catch((err) => {
      console.error(err);
    });
};
