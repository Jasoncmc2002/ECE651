import React, { Component } from 'react';
import { connect } from 'react-redux';
import ContendCard from './contents/ContentCard';
import { Link } from 'react-router-dom';
import GET_INFO_TIMEOUT from './config/GetInfoTimeoutConfig';
import BACKEND_ADDRESS_URL from './config/BackendAddressURLConfig';
import $ from 'jquery';
import PAGE_ITEM_LIMIT from './config/PageItemLimit';

class ProblemSet extends Component {
    state = {
        active_problem_set_list: [
            // { problem_set_id: '1', ps_name: '考试1', ps_start_time: '2024-03-29T22:18', ps_end_time: '2024-03-29T22:18', duration: '60', ps_author_name: 'yw' },
            // { problem_set_id: '2', ps_name: '作业1', ps_start_time: '2024-03-29T22:18', ps_end_time: '2024-03-29T22:18', duration: '0', ps_author_name: 'yw' },
        ],

        page_number: 0, // 从0开始

        is_loading: false
    }

    componentDidMount = () => {
        if (this.props.is_login) {
            this.handleGetActiveProblemSet();
        } else {
            setTimeout(this.handleGetActiveProblemSet, GET_INFO_TIMEOUT);
        }
    }

    handleGetActiveProblemSet = () => {
        // console.log("get active problem set");
        const token = this.props.token;
        // console.log(token);
        this.setState({ is_loading: true });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/problem_set/active/",
            type: "GET",
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    active_problem_set_list: resp,
                    is_loading: false
                });
                if (resp.error_message) {
                    console.log(resp);
                }
            }
        });
    }

    handleLoadingTrRender = () => {
        if (this.state.is_loading) {
            return (
                <tr>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                    <td>
                        <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                    </td>
                </tr>
            );
        }
    }

    renderTable = () => {
        const active_problem_set_list_display = [];
        for (let i = this.state.page_number * PAGE_ITEM_LIMIT; i < this.state.active_problem_set_list.length && i < (this.state.page_number + 1) * PAGE_ITEM_LIMIT; i++) {
            const problem_set = this.state.active_problem_set_list[i];
            active_problem_set_list_display.push({
                ...problem_set
            });
        }

        return (
            <table className="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">Name</th>
                        <th scope="col">Start Time</th>
                        <th scope="col">End Time</th>
                        <th scope="col">Duration</th>
                        <th scope="col">Author</th>
                    </tr>
                </thead>
                <tbody>
                    {this.handleLoadingTrRender()}
                    {active_problem_set_list_display.map((problem_set) => {
                        if (!problem_set.problem_set_id) {
                            return;
                        }
                        const ps_start_time = new Date(problem_set.ps_start_time);
                        const ps_end_time = new Date(problem_set.ps_end_time);
                        const duration = problem_set.duration === '0' ? "N/A" : (problem_set.duration + " min");
                        return (
                            <tr key={'ps' + problem_set.problem_set_id}>
                                <td style={{ overflow: "hidden", textOverflow: "ellipsis", maxWidth: "20vw", minWidth: "20vw", textWrap: "nowrap" }}>
                                    <Link className='link-primary' to={`/problem_set/student_view/one/${problem_set.problem_set_id}/`}>
                                        {problem_set.ps_name}
                                    </Link>
                                </td>
                                <td style={{ maxWidth: "7vw" }}>{ps_start_time.toLocaleString('zh-CN')}</td>
                                <td style={{ maxWidth: "7vw" }}>{ps_end_time.toLocaleString('zh-CN')}</td>
                                <td>{duration}</td>
                                <td>{problem_set.ps_author_name}</td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        );
    }

    renderPreviousButton = () => {
        const page_number = this.state.page_number;
        if (this.state.page_number <= 0) {
            return (
                <button type="button" className="btn btn-outline-secondary" disabled>&laquo;</button>
                // <li className="page-item">
                //     <a className="page-link disabled" href="#" aria-label="Previous">
                //         <span aria-hidden="true">&laquo;</span>
                //     </a>
                // </li>
            );
        } else {
            return (
                <button type="button" className="btn btn-outline-primary" onClick={() => { this.setState({ page_number: page_number - 1 }) }}>&laquo;</button>
                // <li className="page-item">
                //     <a className="page-link" href="#" aria-label="Previous" onClick={() => { this.setState({ page_number: page_number - 1 }) }}>
                //         <span aria-hidden="true">&laquo;</span>
                //     </a>
                // </li>
            );
        }
    }

    renderNextButton = () => {
        const page_number = this.state.page_number;
        const total_page_count = Math.ceil(this.state.active_problem_set_list.length / PAGE_ITEM_LIMIT);
        // console.log(page_number, total_page_count);
        if (page_number >= total_page_count - 1) {
            return (
                <button type="button" className="btn btn-outline-secondary" disabled>&raquo;</button>
                // <li className="page-item">
                //     <a className="page-link disabled" href="#" aria-label="Next">
                //         <span aria-hidden="true">&raquo;</span>
                //     </a>
                // </li>
            );
        } else {
            return (
                <button type="button" className="btn btn-outline-primary" onClick={() => { this.setState({ page_number: page_number + 1 }) }}>&raquo;</button>
                // <li className="page-item">
                //     <a className="page-link" href="#" aria-label="Next" onClick={() => { this.setState({ page_number: page_number + 1 }) }}>
                //         <span aria-hidden="true">&raquo;</span>
                //     </a>
                // </li>
            );
        }
    }

    renderPageButtons = () => {
        const total_page_count = this.state.active_problem_set_list.length === 0 ? 1 : Math.ceil(this.state.active_problem_set_list.length / PAGE_ITEM_LIMIT);
        const page_number = this.state.page_number;
        const page_array = Array.from(Array(total_page_count), (_, x) => x);
        // console.log(page_array);
        return (
            <React.Fragment>
                {page_array.map((page) => {
                    if (page === page_number) {
                        return (
                            <button type="button" key={'page' + page} className="btn btn-primary" onClick={() => { this.setState({ page_number: page }) }}>
                                {page + 1}
                            </button>
                            // <li className="page-item active" key={'page' + page}>
                            //     <a className="page-link" href="#">
                            //         {page + 1}
                            //     </a>
                            // </li>
                        );
                    } else {
                        return (
                            <button type="button" key={'page' + page} className="btn btn-outline-primary" onClick={() => { this.setState({ page_number: page }) }}>
                                {page + 1}
                            </button>
                            // <li className="page-item" key={'page' + page}>
                            //     <a className="page-link" href="#" onClick={() => { this.setState({ page_number: page }) }}>
                            //         {page + 1}
                            //     </a>
                            // </li>
                        );
                    }
                })}
            </React.Fragment>
        );
    }

    handleActiveProblemSetListRender = () => {
        return (
            <div className="container">
                <ContendCard>
                    {/* nav tabs */}
                    <ul className="nav nav-tabs justify-content-center">
                        <li className="nav-item">
                            <Link className="nav-link active" to="/problem_set/student_view/">Active Problem Sets</Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link" to="/problem_set/student_view/all/">All Problem Sets</Link>
                        </li>
                    </ul>

                    {/* problem set table */}
                    {this.renderTable()}

                    {/* button pagination */}
                    <div className='pagination justify-content-center flex-wrap'>
                        <div className="btn-toolbar" role="toolbar" aria-label="Toolbar with button groups">
                            <div className="btn-group me-2" role="group">
                                {this.renderPreviousButton()}
                                {this.renderPageButtons()}
                                {this.renderNextButton()}
                            </div>
                        </div>
                    </div>
                    {/* <nav aria-label="Page navigation example">
                        <ul className="pagination justify-content-center flex-wrap">
                            {this.renderPreviousButton()}
                            {this.renderPageButtons()}
                            {this.renderNextButton()}
                        </ul>
                    </nav> */}
                </ContendCard>
            </div>
        );
    }

    handleAccountRender = () => {
        if (this.props.is_login) {
            return (
                <React.Fragment>
                    {this.handleActiveProblemSetListRender()}
                </React.Fragment>
            );
        } else {
            return (
                <div className="container">
                    <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                        <div className="col col-md-7">
                            <ContendCard>
                                <div className="row justify-content-md-center">
                                    <h1 className="text-center">
                                        View Active Problem Sets
                                    </h1>
                                    <hr />
                                    <h4 className="text-center">Please <Link className='btn btn-link px-0' to="/login/" style={{ textDecoration: "none" }}><h4 className='mb-1'>sign in</h4></Link> to access</h4>
                                </div>
                            </ContendCard>
                        </div>
                    </div>
                </div>
            );
        }
    }

    render() {
        return (
            <React.Fragment>
                {this.handleAccountRender()}
            </React.Fragment>
        );
    }
}

const mapStateToProps = (state, props) => {
    return {
        ...props,
        user_id: state.user_id,
        username: state.username,
        name: state.name,
        permission: state.permission,
        token: state.token,
        is_login: state.is_login,
    };
};

export default connect(mapStateToProps, null)(ProblemSet);