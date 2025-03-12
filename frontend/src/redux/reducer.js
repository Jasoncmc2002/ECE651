import ACTIONS from "./actions";

const reducer = (state = {
    user_id: "",
    username: "",
    name: "",
    permission: "",
    token: "",
    is_login: false,
    // refresh: "",
    // access: "",
}, action) => {
    switch (action.type) {
        case ACTIONS.UPDATE_USER: {
            return {
                ...state,
                user_id: action.user_id,
                username: action.username,
                name: action.name,
                permission: action.permission,
                is_login: true,
            };
        }
        case ACTIONS.UPDATE_TOKEN: {
            return {
                ...state,
                token: action.token,
            };
        }
        case ACTIONS.LOGOUT: {
            localStorage.removeItem("token");
            return {
                user_id: "",
                username: "",
                name: "",
                permission: "",
                token: "",
                is_login: false,
            }
        }
        default: {
            return state;
        }
    }
};

export default reducer;
